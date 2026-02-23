package es.codeurjc.mokaf.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.mokaf.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Para renderizar en Mustache sin LazyInitialization al acceder a review.user.name
    @EntityGraph(attributePaths = {"user"})
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);
}