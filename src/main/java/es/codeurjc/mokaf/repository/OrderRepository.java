package es.codeurjc.mokaf.repository;

import es.codeurjc.mokaf.model.Order;
import es.codeurjc.mokaf.model.Order.Status;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"items", "items.product", "branch"})
    List<Order> findByStatus(Status status);
}