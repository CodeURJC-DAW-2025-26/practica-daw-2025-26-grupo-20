package es.codeurjc.mokaf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.mokaf.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = {"user"})
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);

    Optional<Review> findByIdAndProductId(Long reviewId, Long productId);

    @EntityGraph(attributePaths = {"user"})
    Page<Review> findByProductId(Long productId, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    List<Review> findTop3ByProductIdOrderByCreatedAtDesc(Long productId);
}