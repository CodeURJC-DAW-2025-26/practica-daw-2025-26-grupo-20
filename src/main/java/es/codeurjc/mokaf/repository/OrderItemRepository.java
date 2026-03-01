package es.codeurjc.mokaf.repository;

import es.codeurjc.mokaf.model.OrderItem;
import es.codeurjc.mokaf.model.Product;
import jakarta.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    

    /**
     * Top products ordered by a specific user (across PAID orders),
     * sorted by total quantity descending.
     */
    @Query("SELECT oi.product FROM OrderItem oi " +
            "WHERE oi.order.user.id = :userId AND oi.order.status = 'PAID' " +
            "GROUP BY oi.product " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<Product> findTopProductsByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Top best-selling products globally (across all PAID orders),
     * sorted by total quantity descending.
     */
    @Query("SELECT oi.product FROM OrderItem oi " +
            "WHERE oi.order.status = 'PAID' " +
            "GROUP BY oi.product " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<Product> findTopBestSellingProducts(Pageable pageable);

    //Find specific item in cart
    Optional<OrderItem> findByOrderIdAndProductId(Long orderId, Long productId);
    

    //Top products ordered by a specific user (PAID)
    @Modifying
    @Transactional
    @Query("DELETE FROM OrderItem oi WHERE oi.order.id = :orderId")
    void deleteByOrderId(@Param("orderId") Long orderId);

    

    
    /**
     * Get total quantity sold for a product
     * ADD THIS METHOD - useful for analytics
     */
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi " +
           "WHERE oi.product.id = :productId AND oi.order.status = 'PAID'")
    Long getTotalSoldQuantity(@Param("productId") Long productId);
}