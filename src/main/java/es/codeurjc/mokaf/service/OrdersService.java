package es.codeurjc.mokaf.service;

import es.codeurjc.mokaf.model.Order;
import es.codeurjc.mokaf.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrdersService {

    private final OrderRepository orderRepository;
    private final OrderEmailService orderEmailService;

    public OrdersService(OrderRepository orderRepository, OrderEmailService orderEmailService) {
        this.orderRepository = orderRepository;
        this.orderEmailService = orderEmailService;
    }

    // Todas las órdenes pagadas (para admin)
    public Page<Order> getPaidOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findByStatusOrderByIdDesc(Order.Status.PAID, pageable);
    }

    // Solo las órdenes de un usuario (para usuarios normales)
    public Page<Order> getPaidOrdersByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findByUserIdAndStatusOrderByIdDesc(userId, Order.Status.PAID, pageable);
    }

    public Order getCartForUser(Long userId) {
        return orderRepository.findFirstByUserIdAndStatus(userId, Order.Status.CART);
    }

    public boolean processCheckout(Long userId) {
        Order cart = getCartForUser(userId);
        if (cart != null && !cart.getItems().isEmpty()) {
            cart.setStatus(Order.Status.PAID);
            cart.setPaidAt(java.time.LocalDateTime.now());
            orderRepository.save(cart);

            // Generate PDF and send confirmation email
            orderEmailService.sendOrderConfirmationWithPdf(cart);
            return true;
        }
        return false;
    }
}