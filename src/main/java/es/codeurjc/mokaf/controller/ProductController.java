package es.codeurjc.mokaf.controller;

import es.codeurjc.mokaf.model.Product;
import es.codeurjc.mokaf.model.Review;
import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.service.ProductService;
import es.codeurjc.mokaf.service.ReviewService;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductController {

    private static final int REVIEWS_PAGE_SIZE = 6;

    private final ProductService productService;
    private final ReviewService reviewService;

    public ProductController(ProductService productService, ReviewService reviewService) {
        this.productService = productService;
        this.reviewService = reviewService;
    }

    @GetMapping("/product/{id}")
    public String product(
            @PathVariable Long id,
            Model model,
            @AuthenticationPrincipal User currentUser) {
        Product p = productService.findWithImageById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));

        Page<Review> reviewPage = reviewService.getReviewsPage(id, 0, REVIEWS_PAGE_SIZE);

        model.addAttribute("product", p);
        model.addAttribute("currentPage", "product");

        model.addAttribute("productId", id);
        model.addAttribute("reviews", reviewPage.getContent());
        model.addAttribute("hasMore", reviewPage.hasNext());

        if (currentUser != null) {
            model.addAttribute("user", currentUser);
            model.addAttribute("isAdmin", currentUser.getRole() == User.Role.ADMIN);
        }

        return "product";
    }

    @GetMapping("/api/product/{id}/reviews")
    public String getProductReviews(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            Model model,
            @AuthenticationPrincipal User currentUser) {
        if (page < 0)
            page = 0;

        Page<Review> reviewPage = reviewService.getReviewsPage(id, page, REVIEWS_PAGE_SIZE);

        model.addAttribute("productId", id);
        model.addAttribute("reviews", reviewPage.getContent());
        model.addAttribute("hasMore", reviewPage.hasNext());

        if (currentUser != null) {
            model.addAttribute("user", currentUser);
            model.addAttribute("isAdmin", currentUser.getRole() == User.Role.ADMIN);
        }

        return "fragments/review_items_fragment";
    }

    @PostMapping("/product/{id}/reviews")
    public String createReview(@PathVariable Long id,
            @RequestParam int stars,
            @RequestParam String text,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes ra) {

        if (currentUser == null)
            return "redirect:/login";

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

        if (currentUser == null)
            return "redirect:/login";

        try {
            reviewService.deleteReview(productId, reviewId, currentUser);
            ra.addFlashAttribute("reviewSuccess", "Reseña eliminada.");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("reviewError", ex.getMessage());
        }

        return "redirect:/product/" + productId + "#reviews";
    }
}