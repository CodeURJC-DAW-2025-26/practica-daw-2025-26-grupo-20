package es.codeurjc.mokaf.controller;

import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.repository.ProductRepository;
import es.codeurjc.mokaf.service.ProductService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

    private final ProductRepository productRepository;
    private final ProductService productService;

    public MenuController(ProductRepository productRepository,
            @Qualifier("applicationProductService") ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }

    @GetMapping("/menu")
    public String showMenu(Model model, Authentication authentication) {
        model.addAttribute("title", "Menú");
        model.addAttribute("items", productRepository.findAll());
        model.addAttribute("currentPage", "menu");

        // Recommended products
        int recommendedLimit = 4;
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            model.addAttribute("recommendedItems",
                    productService.getRecommendedProducts(user.getId(), recommendedLimit));
            model.addAttribute("recommendedTitle", "Recomendados para ti");
        } else {
            model.addAttribute("recommendedItems",
                    productService.getBestSellingProducts(recommendedLimit));
            model.addAttribute("recommendedTitle", "Los más vendidos");
        }

        return "menu";
    }
}
