package es.codeurjc.mokaf.service;

import es.codeurjc.mokaf.model.Order;
import es.codeurjc.mokaf.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdersService {

    private final OrderRepository orderRepository;

    public OrdersService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getPaidOrders() {
        return orderRepository.findByStatus(Order.Status.PAID);
    }
}
