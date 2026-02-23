package es.codeurjc.mokaf.controller;

import es.codeurjc.mokaf.model.Product;
import es.codeurjc.mokaf.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/product/{id}")
    public String product(@PathVariable Long id, Model model) {

        Product p = productRepository.findWithReviewsById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));

        model.addAttribute("product", p);
        model.addAttribute("currentPage", "product");
        return "product";
    }
}
