package es.codeurjc.mokaf.repository;

import es.codeurjc.mokaf.model.Order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByStatusOrderByIdDesc(Order.Status status, Pageable pageable);

    Page<Order> findByUserIdAndStatusOrderByIdDesc(Long userId, Order.Status status, Pageable pageable);

    Optional<Order> findByUserIdAndStatus(Long userId, Order.Status status);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.user.id = :userId AND o.status = :status")
    Optional<Order> findByUserIdAndStatusWithItems(@Param("userId") Long userId, @Param("status") Order.Status status);

    List<Order> findAllByUserIdAndStatus(Long userId, Order.Status status);

    Order findFirstByUserIdAndStatus(Long userId, Order.Status status);
}