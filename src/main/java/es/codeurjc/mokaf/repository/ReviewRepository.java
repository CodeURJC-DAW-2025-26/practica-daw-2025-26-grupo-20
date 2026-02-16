package es.codeurjc.mokaf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.codeurjc.mokaf.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {}
