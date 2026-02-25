package es.codeurjc.mokaf.controller;

import es.codeurjc.mokaf.model.Branch;
import es.codeurjc.mokaf.model.Order;
import es.codeurjc.mokaf.model.OrderItem;
import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Show cart page
     */
    @GetMapping
    public String showCart(Model model, @AuthenticationPrincipal User user,
            @RequestParam(required = false) String success) {
        model.addAttribute("title", "Carrito de Compra - Mokaf");
        model.addAttribute("currentPage", "cart");

        if (success != null) {
            model.addAttribute("successMessage", "¡Proceso realizado con éxito!");
        }

        if (user == null) {
            // User not logged in
            model.addAttribute("subtotal", "0.00€");
            model.addAttribute("tax", "0.00€");
            model.addAttribute("total", "0.00€");
            model.addAttribute("cartItems", new ArrayList<>());
            model.addAttribute("itemCount", 0);
            model.addAttribute("totalUnits", 0);
        } else {
            // User logged in
            CartService.CartSummary summary = cartService.getCartSummary(user.getId());
            int totalUnits = cartService.getCartItemCount(user.getId());

            model.addAttribute("subtotal", summary.getSubtotal());
            model.addAttribute("tax", summary.getTax());
            model.addAttribute("total", summary.getTotal());
            model.addAttribute("cartItems", summary.getCart().getItems());
            model.addAttribute("itemCount", summary.getItemCount());
            model.addAttribute("totalUnits", totalUnits);
        }

        return "cart";
    }


    //Add product to cart    
    @PostMapping("/add")
    @ResponseBody
    public Map<String, Object> addToCart(@RequestParam("productId") Long productId,
            @RequestParam(value = "qty", defaultValue = "1") int quantity,
            @AuthenticationPrincipal User user,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (user == null) {
                response.put("success", false);
                response.put("message", "Por favor, inicia sesión para añadir productos al carrito");
                response.put("redirect", "/login");
                return response;
            }

            // Add to cart
            Long branchId = getDefaultBranchId(session);
            OrderItem item = cartService.addToCart(user.getId(), productId, quantity, branchId);

            // Get updated cart info
            CartService.CartSummary summary = cartService.getCartSummary(user.getId());
            int totalUnits = cartService.getCartItemCount(user.getId()); // ← SUMA de cantidades

            response.put("success", true);
            response.put("message", "Producto añadido al carrito");
            response.put("cartCount", totalUnits); // ← AHORA USA totalUnits
            response.put("cartTotal", summary.getTotal());
            response.put("subtotal", summary.getSubtotal());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }

        return response;
    }

    
     //Get cart count
    @GetMapping("/count")
    @ResponseBody
    public Map<String, Object> getCartCount(@AuthenticationPrincipal User user) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (user == null) {
                response.put("success", true);
                response.put("count", 0);
            } else {
                int count = cartService.getCartItemCount(user.getId());
                response.put("success", true);
                response.put("count", count);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("count", 0);
        }

        return response;
    }

    
     //Get cart summary
    @GetMapping("/summary")
    @ResponseBody
    public Map<String, Object> getCartSummary(@AuthenticationPrincipal User user) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (user == null) {
                response.put("success", true);
                response.put("items", new ArrayList<>());
                response.put("subtotal", "0.00€");
                response.put("tax", "0.00€");
                response.put("total", "0.00€");
                response.put("itemCount", 0);
                response.put("discountInfo", "");
                response.put("hasDiscount", false);
            } else {
                CartService.CartSummary summary = cartService.getCartSummary(user.getId());

                List<Map<String, Object>> items = summary.getCart().getItems().stream()
                        .map(this::convertItemToMap)
                        .collect(Collectors.toList());

                response.put("success", true);
                response.put("items", items);
                response.put("subtotal", summary.getSubtotal());
                response.put("tax", summary.getTax());
                response.put("total", summary.getTotal());
                response.put("itemCount", summary.getItemCount());
                response.put("discountInfo", summary.getDiscountInfo());
                response.put("hasDiscount", summary.hasDiscount());
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }

    
     // Update item quantity
    @PostMapping("/update")
    @ResponseBody
    public Map<String, Object> updateQuantity(@RequestParam("itemId") Long itemId,
            @RequestParam("quantity") int quantity,
            @AuthenticationPrincipal User user) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (user == null) {
                response.put("success", false);
                response.put("message", "Por favor, inicia sesión");
                return response;
            }


            cartService.updateItemQuantity(user.getId(), itemId, quantity);


            CartService.CartSummary summary = cartService.getCartSummary(user.getId());
            int totalUnits = cartService.getCartItemCount(user.getId()); 

           
            String itemLineTotal = "0.00€";
            if (summary.getCart() != null) {
                Optional<OrderItem> updatedItem = summary.getCart().getItems().stream()
                        .filter(item -> item.getId().equals(itemId))
                        .findFirst();

                if (updatedItem.isPresent()) {
                    itemLineTotal = formatPrice(updatedItem.get().getLineTotal());
                }
            }

            response.put("success", true);
            response.put("cartCount", totalUnits);
            response.put("subtotal", summary.getSubtotal());
            response.put("total", summary.getTotal());
            response.put("itemLineTotal", itemLineTotal);
            response.put("itemCount", summary.getItemCount());

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }

    
     //Remove item from cart

    @PostMapping("/remove")
    @ResponseBody
    public Map<String, Object> removeItem(@RequestParam("itemId") Long itemId,
            @AuthenticationPrincipal User user) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (user == null) {
                response.put("success", false);
                response.put("message", "Por favor, inicia sesión");
                return response;
            }

            cartService.removeItem(user.getId(), itemId);
            CartService.CartSummary summary = cartService.getCartSummary(user.getId());
            int totalUnits = cartService.getCartItemCount(user.getId());

            response.put("success", true);
            response.put("cartCount", totalUnits);
            response.put("subtotal", summary.getSubtotal());
            response.put("total", summary.getTotal());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }

    private Long getDefaultBranchId(HttpSession session) {
        return 1L;
    }

    private Map<String, Object> convertItemToMap(OrderItem item) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", item.getId());
        map.put("productId", item.getProduct().getId());
        map.put("name", item.getProduct().getName());
        map.put("quantity", item.getQuantity());
        map.put("unitPrice", formatPrice(item.getUnitPrice()));
        map.put("lineTotal", formatPrice(item.getLineTotal()));
        map.put("image", "/images/" + item.getProduct().getId());
        return map;
    }

    private String formatPrice(BigDecimal price) {
        return String.format("%.2f€", price);
    }

    @PostMapping("/clear")
    @ResponseBody
    public Map<String, Object> clearCart(@AuthenticationPrincipal User user) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (user == null) {
                response.put("success", false);
                response.put("message", "Usuario no autenticado");
                return response;
            }

            cartService.clearCart(user.getId());

            response.put("success", true);
            response.put("message", "Carrito vaciado correctamente");
            response.put("cartCount", 0); // ← 0, correcto

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al vaciar el carrito: " + e.getMessage());
        }

        return response;
    }

    @GetMapping("/branches")
    @ResponseBody
    public Map<String, Object> getAvailableBranches(@AuthenticationPrincipal User user) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Branch> branches = cartService.getAvailableBranches();

            // converting list to Map
            List<Map<String, Object>> branchList = new ArrayList<>();
            for (Branch branch : branches) {
                Map<String, Object> branchMap = new HashMap<>();
                branchMap.put("id", branch.getId());
                branchMap.put("name", branch.getName());
                branchMap.put("description", branch.getDescription());
                branchList.add(branchMap);
            }

            // getting branch of the cart
            Long currentBranchId = null;
            if (user != null) {
                Optional<Order> cart = cartService.getCartWithItems(user.getId());
                if (cart.isPresent()) {
                    currentBranchId = cart.get().getBranch().getId();
                }
            }

            response.put("success", true);
            response.put("branches", branchList);
            response.put("currentBranchId", currentBranchId);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }

    @PostMapping("/change-branch")
    @ResponseBody
    public Map<String, Object> changeBranch(@RequestParam("branchId") Long branchId,
            @AuthenticationPrincipal User user) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (user == null) {
                response.put("success", false);
                response.put("message", "Por favor, inicia sesión");
                return response;
            }

            Order updatedCart = cartService.changeCartBranch(user.getId(), branchId);
            CartService.CartSummary summary = cartService.getCartSummary(user.getId());

            response.put("success", true);
            response.put("message", "Sucursal actualizada correctamente");
            response.put("branchId", updatedCart.getBranch().getId());
            response.put("branchName", updatedCart.getBranch().getName());
            response.put("branchDiscount", updatedCart.getBranch().getPurchaseDiscountPercent() + "%");
            response.put("subtotal", summary.getSubtotal());
            response.put("total", summary.getTotal());
            response.put("discountInfo", summary.getDiscountInfo()); // ← NUEVO
            response.put("hasDiscount", summary.hasDiscount());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return response;
    }

    @PostMapping("/checkoutProcess")
    @ResponseBody
    public Map<String, Object> processCheckout(@RequestParam("paymentMethod") String paymentMethod,
            @AuthenticationPrincipal User user) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (user == null) {
                response.put("success", false);
                response.put("message", "Por favor, inicia sesión");
                return response;
            }

            // paying method
            if (paymentMethod == null || paymentMethod.isEmpty()) {
                response.put("success", false);
                response.put("message", "Selecciona un método de pago");
                return response;
            }

            // Process checkout
            Order paidOrder = cartService.processCheckout(user.getId(), paymentMethod);

            response.put("success", true);
            response.put("message", "¡Proceso finalizado con éxito!");
            response.put("orderId", paidOrder.getId());
            response.put("total", formatPrice(paidOrder.getTotalAmount()));

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error al procesar el pago: " + e.getMessage());
        }

        return response;
    }
}