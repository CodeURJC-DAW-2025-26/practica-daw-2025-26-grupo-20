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
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    @Operation(summary = "Get paid orders. Admin sees all, user sees only their own.")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<OrderDTO> getPaidOrders(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        User user = resolveUser(authentication); // 401 if is not authenticated

        if (user.getRole() == User.Role.ADMIN) {
            Page<Order> result = ordersService.getPaidOrders(page, size);
            if (result == null) {
                return Page.empty(PageRequest.of(page, size));
            }
            return result.map(orderMapper::toDto);
        } else {
            Page<Order> result = ordersService.getPaidOrdersByUser(user.getId(), page, size);
            if (result == null) {
                return Page.empty(PageRequest.of(page, size));
            }
            return result.map(orderMapper::toDto);
        }
    }

    // ── GET /api/v1/orders/{id} ───────────────────────────────────────────────
    // Admin → can see any order
    // Normal user → only their own orders
    // Not authenticated → 401
    @Operation(summary = "Get order by id. Admin sees any, user only their own.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
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

    // ── POST /api/v1/orders/{orderId}/payments ────────────────────────────────
    // Change Status from CART to PAID. Only owner can pay their order. Admin cannot pay other users' orders.
    // Only owner can pay their order. Admin cannot pay other users' orders.
    @Operation(summary = "Create payment for an order by its id (CART → PAID)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order paid successfully"),
            @ApiResponse(responseCode = "400", description = "Order is not in CART status"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Forbidden — not your order"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/{orderId}/payments")
    public OrderDTO createPayment(@PathVariable Long orderId, Authentication authentication) {

        User user = resolveUser(authentication);

        // search order and verify it exists
        Order order = ordersService.getOrderById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        // Only owner can pay their order
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "You can only pay your own order");
        }

        // Verify CART state
        if (order.getStatus() != Order.Status.CART) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Order " + orderId + " is already " + order.getStatus());
        }

        // Verify that cart is not empty
        if (order.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot checkout an empty cart");
        }

        // Process payment and change status to PAID
        ordersService.processCheckout(order.getUser().getId());

        // Return paid order
        Order paid = ordersService.getOrderById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found after payment"));

        return orderMapper.toDto(paid);
    }

    // ── GET /api/v1/orders/user/{userId} ──────────────────────────────────────
    @Operation(summary = "Get paid orders for a given user id (admin or owner)")
    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<OrderDTO> getOrdersByUserId(
            @PathVariable Long userId,
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        User requester = resolveUser(authentication);

        // Admins can request any user's orders; normal users only their own
        if (requester.getRole() == User.Role.ADMIN || requester.getId().equals(userId)) {
            Page<Order> result = ordersService.getPaidOrdersByUser(userId, page, size);
            if (result == null) {
                return Page.empty(PageRequest.of(page, size));
            }
            return result.map(orderMapper::toDto);
        } else {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN,
                    "You don't have permission to view these orders");
        }
    }

    // ── DELETE /api/v1/orders/{id} ────────────────────────────────────────────
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