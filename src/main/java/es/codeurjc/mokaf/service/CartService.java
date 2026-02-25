package es.codeurjc.mokaf.service;

import es.codeurjc.mokaf.model.*;
import es.codeurjc.mokaf.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;

    public CartService(OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            BranchRepository branchRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.branchRepository = branchRepository;
    }

    /**
     * Get or create an active cart for a user
     */
    public Order getOrCreateCart(Long userId, Long branchId) {
        
        cleanDuplicateCarts(userId);

        // Try to find existing cart
        List<Order> existingCarts = orderRepository.findAllByUserIdAndStatus(userId, Order.Status.CART);

        if (!existingCarts.isEmpty()) {
            // If there are more than one, use the most recent
            if (existingCarts.size() > 1) {
                System.out.println("⚠️ Múltiples carritos encontrados, usando el más reciente");
                cleanDuplicateCarts(userId); // Clean again
                return orderRepository.findByUserIdAndStatus(userId, Order.Status.CART).orElse(existingCarts.get(0));
            }
            return existingCarts.get(0);
        }

        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Get branch
        Branch branch;
        if (branchId != null) {
            branch = branchRepository.findById(branchId)
                    .orElseThrow(() -> new RuntimeException("Branch not found with id: " + branchId));
        } else {
            branch = branchRepository.findFirstBranch()
                    .orElseThrow(() -> new RuntimeException("No branches available"));
        }

        // Create new cart
        Order newCart = new Order();
        newCart.setUser(user);
        newCart.setBranch(branch);
        newCart.setStatus(Order.Status.CART);
        newCart.setSubtotalAmount(BigDecimal.ZERO);
        newCart.setDiscountPercent(BigDecimal.ZERO);
        newCart.setDiscountAmount(BigDecimal.ZERO);
        newCart.setTotalAmount(BigDecimal.ZERO);

        return orderRepository.save(newCart);
    }

    /**
     * Get cart with items loaded (to avoid LazyInitializationException)
     */
    @Transactional(readOnly = true)
    public Optional<Order> getCartWithItems(Long userId) {
        return orderRepository.findByUserIdAndStatusWithItems(userId, Order.Status.CART);
    }

    /**
     * Add product to cart
     */
    @Transactional
    public OrderItem addToCart(Long userId, Long productId, int quantity, Long branchId) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        // Get or create cart
        Order cart = getOrCreateCart(userId, branchId);

        // Get product with image (using your existing repository method)
        Product product = productRepository.findWithReviewsById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        // Check if product already in cart
        Optional<OrderItem> existingItem = orderItemRepository
                .findByOrderIdAndProductId(cart.getId(), productId);

        if (existingItem.isPresent()) {
            // Update existing item
            OrderItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            item.setQuantity(newQuantity);

            BigDecimal lineTotal = item.getFinalUnitPrice()
                    .multiply(BigDecimal.valueOf(newQuantity))
                    .setScale(2, RoundingMode.HALF_UP);
            item.setLineTotal(lineTotal);

            return orderItemRepository.save(item);
        } else {
            // Create new item
            OrderItem newItem = new OrderItem();
            newItem.setOrder(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(product.getPriceBase());
            newItem.setFinalUnitPrice(product.getPriceBase()); // No discount by default

            BigDecimal lineTotal = product.getPriceBase()
                    .multiply(BigDecimal.valueOf(quantity))
                    .setScale(2, RoundingMode.HALF_UP);
            newItem.setLineTotal(lineTotal);

            cart.addItem(newItem);
            OrderItem savedItem = orderItemRepository.save(newItem);

            // Update cart totals
            updateCartTotals(cart);

            return savedItem;
        }
    }

    /**
     * Update item quantity
     */
    @Transactional
    public void updateItemQuantity(Long userId, Long itemId, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        // Get cart
        Order cart = getOrCreateCart(userId, null);

        // Find item
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // Verify item belongs to user's cart
        if (!item.getOrder().getId().equals(cart.getId())) {
            throw new RuntimeException("Item does not belong to user's cart");
        }

        if (quantity == 0) {
            // Remove item
            cart.removeItem(item);
            orderItemRepository.delete(item);
        } else {
            // Update quantity
            item.setQuantity(quantity);
            BigDecimal lineTotal = item.getFinalUnitPrice()
                    .multiply(BigDecimal.valueOf(quantity))
                    .setScale(2, RoundingMode.HALF_UP);
            item.setLineTotal(lineTotal);
            orderItemRepository.save(item);
        }

        // Update cart totals
        updateCartTotals(cart);
    }

    /**
     * Remove item from cart
     */
    @Transactional
    public void removeItem(Long userId, Long itemId) {
        Order cart = getOrCreateCart(userId, null);

        orderItemRepository.findById(itemId).ifPresent(item -> {
            if (item.getOrder().getId().equals(cart.getId())) {
                cart.removeItem(item);
                orderItemRepository.delete(item);
                updateCartTotals(cart);
            }
        });
    }

    /**
     * Clear cart
     */
    @Transactional
    public void clearCart(Long userId) {
        Optional<Order> cartOpt = orderRepository.findByUserIdAndStatus(userId, Order.Status.CART);

        cartOpt.ifPresent(cart -> {
            orderItemRepository.deleteByOrderId(cart.getId());
            cart.getItems().clear();
            cart.setSubtotalAmount(BigDecimal.ZERO);
            cart.setDiscountAmount(BigDecimal.ZERO);
            cart.setTotalAmount(BigDecimal.ZERO);
            orderRepository.save(cart);
        });
    }

    /**
     * Update cart totals
     */
    private void updateCartTotals(Order cart) {
        // Calculate subtotal
        BigDecimal subtotal = cart.getItems().stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        cart.setSubtotalAmount(subtotal);

        // discount init
        BigDecimal discountPercent = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;

        // Getting the discount for the branch
        Branch branch = cart.getBranch();
        if (branch != null && branch.getPurchaseDiscountPercent() != null) {
            discountPercent = branch.getPurchaseDiscountPercent();

            // Calculate rest of price
            discountAmount = subtotal
                    .multiply(discountPercent)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        }

        cart.setDiscountPercent(discountPercent);
        cart.setDiscountAmount(discountAmount);

        // Calculate total
        BigDecimal total = subtotal.subtract(discountAmount)
                .setScale(2, RoundingMode.HALF_UP);
        cart.setTotalAmount(total);

        orderRepository.save(cart);
    }

    private String formatPrice(BigDecimal price) {
        return price.toString() + "€";
    }

    /**
     * Cart Summary inner class
     */
    public static class CartSummary {
        private final Order cart;
        private final String subtotal;
        private final String tax;
        private final String total;
        private final String discountInfo;
        private final int itemCount;

        public CartSummary(Order cart, String subtotal,
                String tax, String total, String discountInfo, int itemCount) {
            this.cart = cart;
            this.subtotal = subtotal;
            this.tax = tax;
            this.total = total;
            this.discountInfo = discountInfo;
            this.itemCount = itemCount;
        }

        // Getters
        public Order getCart() {
            return cart;
        }

        public String getSubtotal() {
            return subtotal;
        }

        public String getTax() {
            return tax;
        }

        public String getTotal() {
            return total;
        }

        public String getDiscountInfo() {
            return discountInfo;
        }

        public int getItemCount() {
            return itemCount;
        } 

        // Helper method to check if cart is empty
        public boolean isEmpty() {
            return cart.getItems() == null || cart.getItems().isEmpty();
        }

        // To know if there is discount
        public boolean hasDiscount() {
            return discountInfo != null && !discountInfo.isEmpty();
        }
    }

    @Transactional(readOnly = true)
    public List<Branch> getAvailableBranches() {
        return branchRepository.findAll();
    }

    @Transactional
    public Order changeCartBranch(Long userId, Long newBranchId) {
        System.out.println("=== changeCartBranch ===");
        System.out.println("userId: " + userId);
        System.out.println("newBranchId: " + newBranchId);

        // Get current cart
        Order cart = orderRepository.findByUserIdAndStatus(userId, Order.Status.CART)
                .orElseThrow(() -> {
                    System.out.println("No se encontró carrito para usuario: " + userId);
                    return new RuntimeException("No active cart found");
                });


        // Get new branch
        Branch newBranch = branchRepository.findById(newBranchId)
                .orElseThrow(() -> {
                    System.out.println("No se encontró sucursal con ID: " + newBranchId);
                    return new RuntimeException("Branch not found with id: " + newBranchId);
                });

        System.out.println("Nueva sucursal: " + newBranch.getName() +
                ", descuento: " + newBranch.getPurchaseDiscountPercent() + "%");

        // Update branch
        cart.setBranch(newBranch);

        // Recalculate totals with new branch discount
        updateCartTotals(cart);

        System.out.println("Nuevo subtotal: " + cart.getSubtotalAmount());
        System.out.println("Nuevo descuento: " + cart.getDiscountAmount() + "€");
        System.out.println("Nuevo total: " + cart.getTotalAmount());

        return orderRepository.save(cart);
    }

    public CartSummary getCartSummary(Long userId) {
        Order cart = getOrCreateCart(userId, null);

        // Calculate tax (21% VAT)
        BigDecimal tax = cart.getSubtotalAmount()
                .multiply(BigDecimal.valueOf(0.21))
                .setScale(2, RoundingMode.HALF_UP);

        // Calculate final total
        BigDecimal finalTotal = cart.getTotalAmount()
                .add(tax)
                .setScale(2, RoundingMode.HALF_UP);

        
        String discountInfo = "";
        if (cart.getDiscountPercent().compareTo(BigDecimal.ZERO) > 0) {
            discountInfo = cart.getDiscountPercent() + "% (" + formatPrice(cart.getDiscountAmount()) + ")";
        }

        return new CartSummary(
                cart,
                formatPrice(cart.getSubtotalAmount()),
                formatPrice(tax),
                formatPrice(finalTotal),
                discountInfo, // ← NUEVO
                cart.getItems().size());
    }

    public int getCartItemCount(Long userId) {
        Optional<Order> cartOpt = orderRepository.findByUserIdAndStatus(userId, Order.Status.CART);

        if (cartOpt.isPresent()) {
            // Add all quantitys
            return cartOpt.get().getItems().stream()
                    .mapToInt(OrderItem::getQuantity)
                    .sum();
        }

        return 0;
    }

    @Transactional
    public Order processCheckout(Long userId, String paymentMethod) {
        System.out.println("=== PROCESANDO CHECKOUT ===");
        System.out.println("Usuario: " + userId);
        System.out.println("Método de pago: " + paymentMethod);

        // Cleaning duplicates
        cleanDuplicateCarts(userId);

        // 1. Getting cart
        Order cart = orderRepository.findByUserIdAndStatus(userId, Order.Status.CART)
                .orElseThrow(() -> new RuntimeException("No hay carrito activo"));

        // 2. Carte not empty
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // 3. Changing state to PAID
        cart.setStatus(Order.Status.PAID);
        cart.setPaidAt(LocalDateTime.now());

        // Saving order in repo
        Order paidOrder = orderRepository.save(cart);
        System.out.println("✅ Orden pagada guardada con ID: " + paidOrder.getId());


        List<Branch> branches = branchRepository.findAll();
        if (branches.isEmpty()) {
            throw new RuntimeException("No hay sucursales disponibles");
        }
        Branch defaultBranch = branches.get(0); // Default branch is the first one

        Order newCart = new Order();
        newCart.setUser(paidOrder.getUser());
        newCart.setBranch(defaultBranch);
        newCart.setStatus(Order.Status.CART);
        newCart.setSubtotalAmount(BigDecimal.ZERO);
        newCart.setDiscountPercent(BigDecimal.ZERO);
        newCart.setDiscountAmount(BigDecimal.ZERO);
        newCart.setTotalAmount(BigDecimal.ZERO);

        orderRepository.save(newCart);
        System.out.println("🛒 Nuevo carrito vacío creado con sucursal: " + defaultBranch.getName());

        return paidOrder;
    }

    public void cleanDuplicateCarts(Long userId) {
        List<Order> carts = orderRepository.findAllByUserIdAndStatus(userId, Order.Status.CART);

        if (carts.size() > 1) {
            System.out.println("⚠️ Se encontraron " + carts.size() + " carritos para el usuario " + userId);

            
            Order keepCart = carts.stream()
                    .max((c1, c2) -> c1.getId().compareTo(c2.getId()))
                    .orElse(carts.get(0));

            for (Order cart : carts) {
                if (!cart.getId().equals(keepCart.getId())) {
                    System.out.println("Eliminando carrito duplicado ID: " + cart.getId());
                    orderItemRepository.deleteByOrderId(cart.getId());
                    orderRepository.delete(cart);
                }
            }
        }
    }
}