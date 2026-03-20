package es.codeurjc.mokaf.api.controller;

import es.codeurjc.mokaf.api.dto.OrderDTO;
import es.codeurjc.mokaf.api.exception.ResourceNotFoundException;
import es.codeurjc.mokaf.api.mapper.OrderMapper;
import es.codeurjc.mokaf.model.Order;
import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.service.OrdersService;
import es.codeurjc.mokaf.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderRestController {

    private final OrdersService ordersService;
    private final OrderMapper orderMapper;
    private final UserService userService;

    public OrderRestController(OrdersService ordersService, OrderMapper orderMapper, UserService userService) {
        this.ordersService = ordersService;
        this.orderMapper = orderMapper;
        this.userService = userService;
    }

    // ── GET /api/v1/orders ────────────────────────────────────────────────────
    // Admin → todas las órdenes pagadas
    // Usuario normal → solo las suyas
    // No autenticado → 401
    @Operation(summary = "Get paid orders. Admin sees all, user sees only their own.")
    @GetMapping
    public Page<OrderDTO> getPaidOrders(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        User user = resolveUser(authentication); // lanza 401 si no autenticado

        if (user.getRole() == User.Role.ADMIN) {
            return ordersService.getPaidOrders(page, size)
                    .map(orderMapper::toDto);
        } else {
            return ordersService.getPaidOrdersByUser(user.getId(), page, size)
                    .map(orderMapper::toDto);
        }
    }

    // ── GET /api/v1/orders/{id} ───────────────────────────────────────────────
    // Admin → puede ver cualquier orden
    // Usuario normal → solo si le pertenece
    // No autenticado → 401
    @Operation(summary = "Get order by id. Admin sees any, user only their own.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public OrderDTO getOrder(@PathVariable Long id, Authentication authentication) {

        User user = resolveUser(authentication);

        Order order = ordersService.getOrderById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));

        if (user.getRole() != User.Role.ADMIN &&
                !order.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You don't have permission to view this order");
        }

        return orderMapper.toDto(order);
    }

    // ── POST /api/v1/orders/{orderId}/checkout ────────────────────────────────
    // Cambia el estado de CART a PAID
    // Solo el usuario dueño de la orden puede hacer checkout
    @Operation(summary = "Checkout an order by its id (CART → PAID)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order paid successfully"),
            @ApiResponse(responseCode = "400", description = "Order is not in CART status"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Forbidden — not your order"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/{orderId}/checkout")
    public OrderDTO checkout(@PathVariable Long orderId, Authentication authentication) {

        User user = resolveUser(authentication);

        // Buscar la orden por su ID
        Order order = ordersService.getOrderById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        // Solo el dueño puede hacer checkout
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can only checkout your own order");
        }

        // Verificar que está en estado CART
        if (order.getStatus() != Order.Status.CART) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Order " + orderId + " is already " + order.getStatus());
        }

        // Verificar que tiene items
        if (order.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot checkout an empty cart");
        }

        // Procesar el pago usando el userId del dueño de la orden
        ordersService.processCheckout(order.getUser().getId());

        // Devolver la orden ya pagada
        Order paid = ordersService.getOrderById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found after checkout"));

        return orderMapper.toDto(paid);
    }

    // ── DELETE /api/v1/orders/{id} ────────────────────────────────────────────
    // Solo el ADMIN puede borrar órdenes → 403 si es usuario normal
    @Operation(summary = "Delete an order. Admin only.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order deleted"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Forbidden — admin only"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id, Authentication authentication) {

        User user = resolveUser(authentication);

        if (user.getRole() != User.Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only admins can delete orders");
        }

        ordersService.getOrderById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));

        ordersService.deleteOrder(id);
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private User resolveUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User u) {
            return u;
        }

        if (principal instanceof String email) {
            return userService.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED, "User not found"));
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
    }
}