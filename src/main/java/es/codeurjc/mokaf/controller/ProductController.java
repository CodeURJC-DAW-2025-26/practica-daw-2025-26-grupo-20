package es.codeurjc.mokaf.controller;

import es.codeurjc.mokaf.model.Product;
import es.codeurjc.mokaf.model.Review;
import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.repository.ProductRepository;
import es.codeurjc.mokaf.repository.ReviewRepository;
import es.codeurjc.mokaf.service.ReviewService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class ProductController {

    private final ProductRepository productRepository;
    private final ReviewService reviewService;

    public ProductController(ProductRepository productRepository, ReviewService reviewService) {
        this.productRepository = productRepository;
        this.reviewService = reviewService;
    }

    @GetMapping("/product/{id}")
    public String product(@PathVariable Long id, Model model) {

        Product p = productRepository.findWithReviewsById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));

        model.addAttribute("product", p);
        model.addAttribute("currentPage", "product");
        return "product";
    }
    @PostMapping("/product/{id}/reviews")
    public String createReview(@PathVariable Long id,
                               @RequestParam int stars,
                               @RequestParam String text,
                               @AuthenticationPrincipal User currentUser,
                               RedirectAttributes ra) {

        if (currentUser == null) return "redirect:/login";

        try {
            reviewService.createReview(id, currentUser, stars, text);
            ra.addFlashAttribute("reviewSuccess", "Reseña publicada.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("reviewError", ex.getMessage());
        }

        return "redirect:/product/" + id + "#reviews";
    }

    @PostMapping("/product/{productId}/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable Long productId,
                               @PathVariable Long reviewId,
                               @AuthenticationPrincipal User currentUser,
                               RedirectAttributes ra) {

        if (currentUser == null) return "redirect:/login";

        try {
            reviewService.deleteReview(productId, reviewId, currentUser);
            ra.addFlashAttribute("reviewSuccess", "Reseña eliminada.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("reviewError", ex.getMessage());
        }

        return "redirect:/product/" + productId + "#reviews";
    }
}
    

