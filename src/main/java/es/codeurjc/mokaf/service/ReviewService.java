package es.codeurjc.mokaf.service;

import es.codeurjc.mokaf.model.Product;
import es.codeurjc.mokaf.model.Review;
import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.repository.ProductRepository;
import es.codeurjc.mokaf.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public ReviewService(ReviewRepository reviewRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Review createReview(Long productId, User author, int stars, String text) {

        if (author == null)
            throw new IllegalArgumentException("Debes iniciar sesión.");
        if (stars < 1 || stars > 5)
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5.");
        if (text == null || text.isBlank())
            throw new IllegalArgumentException("El texto de la reseña no puede estar vacío.");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + productId));

        Review r = new Review();
        r.setUser(author);
        r.setProduct(product);
        r.setStars(stars);
        r.setText(text.trim());

        return reviewRepository.save(r);
    }

    @Transactional
    public void deleteReview(Long productId, Long reviewId, User requester) {

        if (requester == null)
            throw new IllegalArgumentException("Debes iniciar sesión.");
        if (requester.getRole() != User.Role.ADMIN)
            throw new IllegalArgumentException("No tienes permisos para eliminar reseñas.");

        Review r = reviewRepository.findByIdAndProductId(reviewId, productId)
                .orElseThrow(() -> new IllegalArgumentException("Reseña no encontrada."));

        reviewRepository.delete(r);
    }

    @Transactional(readOnly = true)
    public Page<Review> getReviewsPage(Long productId, int page, int size) {
        return reviewRepository.findByProductId(
                productId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt", "id")));
    }

}