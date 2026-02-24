package es.codeurjc.mokaf.service;

import es.codeurjc.mokaf.model.OrderItem;
import es.codeurjc.mokaf.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    public List<OrderItem> getItemsByOrder(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }
}