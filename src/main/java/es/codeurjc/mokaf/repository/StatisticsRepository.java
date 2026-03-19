package es.codeurjc.mokaf.repository;

import es.codeurjc.mokaf.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatisticsRepository extends JpaRepository<Product, Long> {

    // Chart1
    @Query(value = "SELECT " +
            "p.id AS productId, " + 
            "p.name AS productName, " +
            "p.category AS category, " +
            "SUM(oi.quantity) AS totalUnits, " +
            "SUM(oi.line_total) AS totalAmount " +
            "FROM mokaf_orders o " +  // ← CORR
            "JOIN order_items oi ON o.id = oi.order_id " +
            "JOIN products p ON oi.product_id = p.id " +
            "WHERE o.status = 'PAID' " +
            "AND o.paid_at IS NOT NULL " +
            "AND MONTH(o.paid_at) = MONTH(CURRENT_DATE()) " +
            "AND YEAR(o.paid_at) = YEAR(CURRENT_DATE()) " +
            "GROUP BY p.id, p.name, p.category " +
            "ORDER BY totalUnits DESC " +
            "LIMIT 1", nativeQuery = true)
    List<Object[]> findBestSellingProductCurrentMonth();

    // Chart 2 
    @Query(value = "SELECT " +
            "p.category AS category, " +
            "SUM(oi.quantity) AS totalUnits, " +
            "SUM(oi.line_total) AS totalAmount, " +
            "COUNT(DISTINCT o.id) AS orderCount " +
            "FROM mokaf_orders o " + 
            "JOIN order_items oi ON o.id = oi.order_id " +
            "JOIN products p ON oi.product_id = p.id " +
            "WHERE o.status = 'PAID' " +
            "AND o.paid_at IS NOT NULL " +
            "AND o.paid_at >= DATE_SUB(CURRENT_DATE(), INTERVAL 3 MONTH) " +
            "GROUP BY p.category " +
            "ORDER BY totalUnits DESC", nativeQuery = true)
    List<Object[]> findTopCategoryLast3Months();

    // Chart 3 
    @Query(value = "SELECT " +
            "b.name AS branchName, " +
            "COUNT(DISTINCT o.id) AS totalOrders, " +
            "SUM(oi.quantity) AS totalUnits, " +
            "SUM(o.total_amount) AS totalRevenue, " +
            "AVG(o.total_amount) AS avgOrderValue " +
            "FROM branches b " +
            "JOIN mokaf_orders o ON b.id = o.branch_id " +  
            "JOIN order_items oi ON o.id = oi.order_id " +
            "WHERE o.status = 'PAID' " +
            "AND o.paid_at IS NOT NULL " +
            "GROUP BY b.id, b.name " +
            "ORDER BY totalRevenue DESC " +
            "LIMIT 1", nativeQuery = true)
    List<Object[]> findTopBranch();

   
    @Query(value = "SELECT " +
            "b.name AS branchName, " +
            "COUNT(DISTINCT o.id) AS totalOrders, " +
            "SUM(oi.quantity) AS totalUnits, " +
            "SUM(o.total_amount) AS totalRevenue " +
            "FROM branches b " +
            "LEFT JOIN mokaf_orders o ON b.id = o.branch_id AND o.status = 'PAID' AND o.paid_at IS NOT NULL " +  // ← CORREGIDO
            "LEFT JOIN order_items oi ON o.id = oi.order_id " +
            "GROUP BY b.id, b.name " +
            "ORDER BY totalRevenue DESC", nativeQuery = true)
    List<Object[]> findAllBranchesSales();

    @Query(value = "SELECT DISTINCT p.category FROM products p ORDER BY p.category", nativeQuery = true)
    List<String> findAllProductCategories();

    Optional<Product> findByName(String name);

    // Product with highest rating
    @Query(value = "SELECT " +
           "p.id, " +
           "p.name, " +
           "p.category, " +
           "AVG(r.stars) AS averageRating, " +
           "COUNT(r.id) AS reviewCount " +
           "FROM products p " +
           "JOIN reviews r ON p.id = r.product_id " +
           "WHERE r.created_at >= DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH) " +
           "GROUP BY p.id, p.name, p.category " +
           "HAVING COUNT(r.id) >= 3 " +
           "ORDER BY averageRating DESC, reviewCount DESC " +
           "LIMIT 1", nativeQuery = true)
    List<Object[]> findTopRatedProductLastMonth();
}