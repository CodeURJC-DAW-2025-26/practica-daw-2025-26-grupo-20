package es.codeurjc.mokaf.repository;

import es.codeurjc.mokaf.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByStatusOrderByIdDesc(Order.Status status, Pageable pageable);

    Page<Order> findByUserIdAndStatusOrderByIdDesc(Long userId, Order.Status status, Pageable pageable);

    Order findFirstByUserIdAndStatus(Long userId, Order.Status status);
}