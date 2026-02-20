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

    // Mejor: /product/1, /product/2, etc.
    @GetMapping("/product/{id}")
    public String product(@PathVariable Long id, Model model) {

        Product p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));

        // Fuerza la inicialización del proxy LAZY para que Mustache pueda leer image.id
        if (p.getImage() != null) {
            p.getImage().getId();
        }

        model.addAttribute("product", p);
        model.addAttribute("currentPage", "product");
        return "product";
    }
}
