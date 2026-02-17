package es.codeurjc.mokaf.controller;

import es.codeurjc.mokaf.model.Category;
import es.codeurjc.mokaf.model.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;

@Controller
public class ProductController {

    @GetMapping("/product")
    public String product(Model model) {
        System.out.println(">>> ENTRANDO EN ProductController /product");

        Product p = new Product(
                "Expreso",
                "Café negro fuerte y aromático.",
                null, // Image (BLOB) todavía no la estamos cargando aquí
                new BigDecimal("2.50"),
                Category.HOT
        );

        model.addAttribute("title", "Producto · MokaF");
        model.addAttribute("product", p);
        model.addAttribute("currentPage", "product");
        return "product";
    }
}
