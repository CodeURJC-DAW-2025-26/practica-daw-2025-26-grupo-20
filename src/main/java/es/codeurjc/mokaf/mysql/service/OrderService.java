package es.codeurjc.mokaf.mysql.service;

import es.codeurjc.mokaf.mysql.model.Order;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("mysqlOrderService")
public class OrderService {

    private final List<Order> orders = new ArrayList<>();

    public OrderService() {
        // Init with some dummy orders if needed
    }

    public List<Order> findAll() {
        return orders;
    }

    public Optional<Order> findById(Long id) {
        return orders.stream().filter(o -> o.getId().equals(id)).findFirst();
    }

    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId((long) (orders.size() + 1));
        }
        orders.add(order);
        return order;
    }
}
