package es.codeurjc.mokaf.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.codeurjc.mokaf.model.Order;
import es.codeurjc.mokaf.repository.OrderRepository;

@Service
public class OrdersService {

    private final OrderRepository orderRepository;

    public OrdersService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Page<Order> getPaidOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findByStatusOrderByIdDesc(Order.Status.PAID, pageable);
    }
}