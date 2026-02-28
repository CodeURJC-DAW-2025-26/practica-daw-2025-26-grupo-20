package es.codeurjc.mokaf.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.mokaf.model.Category;
import es.codeurjc.mokaf.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = { "image", "reviews", "reviews.user" })
    Optional<Product> findWithReviewsById(Long id);

    @EntityGraph(attributePaths = { "image", "allergens" })
    Optional<Product> findWithImageById(Long id);

    Page<Product> findByCategory(Category category, Pageable pageable);
}