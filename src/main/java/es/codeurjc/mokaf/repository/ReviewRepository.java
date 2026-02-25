package es.codeurjc.mokaf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.codeurjc.mokaf.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @EntityGraph(attributePaths = {"user"})
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);

    Optional<Review> findByIdAndProductId(Long reviewId, Long productId);

    @Query("SELECT r FROM Review r WHERE r.product.id = :productId ORDER BY r.createdAt DESC LIMIT 3")
    List<Review> findTop3ByProductIdOrderByCreatedAtDesc(@Param("productId") Long productId);
}