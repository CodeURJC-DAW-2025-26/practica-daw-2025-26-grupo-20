package es.codeurjc.mokaf.service;

import es.codeurjc.mokaf.model.Order;
import es.codeurjc.mokaf.repository.OrderRepository;

import java.util.Optional;

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

    // all payed orders only for admin
    public Page<Order> getPaidOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findByStatusOrderByIdDesc(Order.Status.PAID, pageable);
    }

    // paid orders for the actual user
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

    // funciones añadidas de la api

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Optional<Order> getLastPaidOrderByUser(Long userId) {
        return orderRepository
                .findByUserIdAndStatusOrderByIdDesc(userId, Order.Status.PAID, PageRequest.of(0, 1))
                .stream()
                .findFirst();
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

}