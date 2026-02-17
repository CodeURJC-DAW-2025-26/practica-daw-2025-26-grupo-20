package es.codeurjc.mokaf.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import es.codeurjc.mokaf.model.Order;   

public interface OrderRepository extends JpaRepository<Order, Long> {}