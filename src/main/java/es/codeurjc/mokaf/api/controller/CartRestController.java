package es.codeurjc.mokaf.api.controller;

import es.codeurjc.mokaf.api.dto.BranchDTO;
import es.codeurjc.mokaf.api.dto.cartdtos.CartResponseDTO;
import es.codeurjc.mokaf.api.dto.cartdtos.CartSummaryDTO;
import es.codeurjc.mokaf.api.mapper.CartMapper;
import es.codeurjc.mokaf.model.Order;
import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Cart", description = "Shopping cart operations")
@RestController
@RequestMapping("/api/v1/cart")
public class CartRestController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartMapper cartMapper;

    // ============ Helper to verify authentication============

    /**
     * Verify user is authenticated and return the User object. If not authenticated, throw 401.
     * ADMIN and CUSTOMER have access to cart endpoints, but they must be logged in.
     */
    private User resolveAuthenticatedUser(User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, 
                "Debes iniciar sesión para acceder al carrito");
        }
        return user;
    }

    // ============ Endpoints ============

    /**
     * GET /api/v1/cart - Get current cart summary
     */
    @Operation(summary = "Get current user's cart")
    @GetMapping
    public CartSummaryDTO getCart(@AuthenticationPrincipal User user) {
        User authenticatedUser = resolveAuthenticatedUser(user);

        CartService.CartSummary summary = cartService.getCartSummary(authenticatedUser.getId());
        int totalUnits = cartService.getCartItemCount(authenticatedUser.getId());

        return cartMapper.toCartSummaryDTO(summary, totalUnits);
    }

    /**
     * POST /api/v1/cart/items - Add product to cart
     */
    @Operation(summary = "Add product to cart")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product added successfully"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/items")
    public CartResponseDTO addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity,
            @AuthenticationPrincipal User user) {

        User authenticatedUser = resolveAuthenticatedUser(user);

        try {
            cartService.addToCart(authenticatedUser.getId(), productId, quantity, getDefaultBranchId());
            
            CartService.CartSummary summary = cartService.getCartSummary(authenticatedUser.getId());
            int totalUnits = cartService.getCartItemCount(authenticatedUser.getId());
            
            CartSummaryDTO cartSummary = cartMapper.toCartSummaryDTO(summary, totalUnits);
            
            return cartMapper.successResponse(
                "Producto añadido al carrito", cartSummary, totalUnits
            );
            
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * PUT /api/v1/cart/items/{itemId} - Update item quantity
     */
    @Operation(summary = "Update cart item quantity")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Quantity updated"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "400", description = "Invalid quantity")
    })
    @PutMapping("/items/{itemId}")
    public CartResponseDTO updateItemQuantity(
            @PathVariable Long itemId,
            @RequestParam int quantity,
            @AuthenticationPrincipal User user) {

        User authenticatedUser = resolveAuthenticatedUser(user);

        try {
            cartService.updateItemQuantity(authenticatedUser.getId(), itemId, quantity);
            
            CartService.CartSummary summary = cartService.getCartSummary(authenticatedUser.getId());
            int totalUnits = cartService.getCartItemCount(authenticatedUser.getId());
            
            CartSummaryDTO cartSummary = cartMapper.toCartSummaryDTO(summary, totalUnits);
            
            return cartMapper.successResponse(
                "Cantidad actualizada", cartSummary, totalUnits
            );
            
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * DELETE /api/v1/cart/items/{itemId} - Remove item from cart
     */
    @Operation(summary = "Remove item from cart")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item removed"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @DeleteMapping("/items/{itemId}")
    public CartResponseDTO removeItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal User user) {

        User authenticatedUser = resolveAuthenticatedUser(user);

        try {
            cartService.removeItem(authenticatedUser.getId(), itemId);
            
            CartService.CartSummary summary = cartService.getCartSummary(authenticatedUser.getId());
            int totalUnits = cartService.getCartItemCount(authenticatedUser.getId());
            
            CartSummaryDTO cartSummary = cartMapper.toCartSummaryDTO(summary, totalUnits);
            
            return cartMapper.successResponse(
                "Producto eliminado", cartSummary, totalUnits
            );
            
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * DELETE /api/v1/cart - Clear entire cart
     */
    @Operation(summary = "Clear entire cart")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cart cleared"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @DeleteMapping
    public CartResponseDTO clearCart(@AuthenticationPrincipal User user) {

        User authenticatedUser = resolveAuthenticatedUser(user);

        try {
            cartService.clearCart(authenticatedUser.getId());
            
            return cartMapper.successResponseSimple("Carrito vaciado");
            
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * GET /api/v1/cart/branches - Get available branches
     */
    @Operation(summary = "Get available branches for pickup")
    @GetMapping("/branches")
    public List<BranchDTO> getBranches() {
        List<es.codeurjc.mokaf.model.Branch> branches = cartService.getAvailableBranches();
        
        return branches.stream()
            .map(b -> new BranchDTO(
                b.getId(),
                b.getName(),
                b.getDescription(),
                b.getPurchaseDiscountPercent()
            ))
            .collect(Collectors.toList());
    }

    /**
     * GET /api/v1/cart/branch/current - Get current cart branch
     */
    @Operation(summary = "Get current branch of the cart")
    @GetMapping("/branch/current")
    public BranchDTO getCurrentBranch(@AuthenticationPrincipal User user) {

        User authenticatedUser = resolveAuthenticatedUser(user);

        Order cart = cartService.getOrCreateCart(authenticatedUser.getId(), getDefaultBranchId());
        
        es.codeurjc.mokaf.model.Branch branch = cart.getBranch();
        return new BranchDTO(
            branch.getId(),
            branch.getName(),
            branch.getDescription(),
            branch.getPurchaseDiscountPercent()
        );
    }

    /**
     * PUT /api/v1/cart/branch - Change cart branch
     */
    @Operation(summary = "Change the branch for the cart")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Branch changed"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "404", description = "Branch not found")
    })
    @PutMapping("/branch")
    public CartResponseDTO changeBranch(
            @RequestParam Long branchId,
            @AuthenticationPrincipal User user) {

        User authenticatedUser = resolveAuthenticatedUser(user);

        try {
            Order updatedCart = cartService.changeCartBranch(authenticatedUser.getId(), branchId);
            CartService.CartSummary summary = cartService.getCartSummary(authenticatedUser.getId());
            int totalUnits = cartService.getCartItemCount(authenticatedUser.getId());
            
            CartSummaryDTO cartSummary = cartMapper.toCartSummaryDTO(summary, totalUnits);
            
            return cartMapper.successResponse(
                "Sucursal actualizada a: " + updatedCart.getBranch().getName(), 
                cartSummary, totalUnits
            );
            
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * POST /api/v1/cart/payments - Process payment and create order
     */
    @Operation(summary = "Create a payment for the cart and generate order")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Payment processed successfully"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "400", description = "Empty cart or invalid payment method")
    })
    @PostMapping("/payments")
    public CartResponseDTO createPayment(
            @RequestParam String paymentMethod,
            @AuthenticationPrincipal User user) {

        User authenticatedUser = resolveAuthenticatedUser(user);

        if (paymentMethod == null || paymentMethod.isEmpty()) {
            throw new IllegalArgumentException("Selecciona un método de pago");
        }

        try {
            Order paidOrder = cartService.processCheckout(authenticatedUser.getId(), paymentMethod);
            
            return cartMapper.successResponseSimple(
                "¡Pedido realizado con éxito! Orden #" + paidOrder.getId()
            );
            
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    // ============ Helper Methods ============

    private Long getDefaultBranchId() {
        return 1L;
    }
}