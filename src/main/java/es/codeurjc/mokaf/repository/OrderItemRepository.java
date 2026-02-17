package es.codeurjc.mokaf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.codeurjc.mokaf.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {}