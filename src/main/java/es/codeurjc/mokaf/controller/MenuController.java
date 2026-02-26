package es.codeurjc.mokaf.controller;

import es.codeurjc.mokaf.model.Category;
import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.model.Product;
import es.codeurjc.mokaf.repository.ProductRepository;
import es.codeurjc.mokaf.repository.AllergenRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MenuController {

    private final ProductRepository productRepository;
    private final AllergenRepository allergenRepository;

    public MenuController(ProductRepository productRepository,
            AllergenRepository allergenRepository) {
        this.productRepository = productRepository;
        this.allergenRepository = allergenRepository;
    }

    @GetMapping("/menu")
    public String showMenu(Model model, Authentication authentication,
            @RequestParam(required = false) String category) {
        model.addAttribute("title", "Menú");

        Page<Product> productPage;
        if (category != null && !category.isEmpty() && !category.equalsIgnoreCase("all")) {
            try {
                Category cat = Category.valueOf(category.toUpperCase());
                productPage = productRepository.findByCategory(cat, PageRequest.of(0, 6));
                model.addAttribute("selectedCategory", category);
            } catch (IllegalArgumentException e) {
                productPage = productRepository.findAll(PageRequest.of(0, 6));
            }
        } else {
            productPage = productRepository.findAll(PageRequest.of(0, 6));
        }

        model.addAttribute("items", productPage.getContent());
        model.addAttribute("hasMore", productPage.hasNext());

        model.addAttribute("categories", Category.values());
        model.addAttribute("allergens", allergenRepository.findAll());
        model.addAttribute("currentPage", "menu");

        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            boolean isAdmin = user.getRole() == User.Role.ADMIN;
            model.addAttribute("user", user);
            model.addAttribute("isAdmin", isAdmin);
        }

        return "menu";
    }

    @GetMapping("/api/menu")
    public String getMenuItems(Model model, @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String category, Authentication authentication) {

        Page<Product> productPage;
        if (category != null && !category.isEmpty() && !category.equalsIgnoreCase("all")) {
            try {
                Category cat = Category.valueOf(category.toUpperCase());
                productPage = productRepository.findByCategory(cat, PageRequest.of(page, 6));
            } catch (IllegalArgumentException e) {
                productPage = productRepository.findAll(PageRequest.of(page, 6));
            }
        } else {
            productPage = productRepository.findAll(PageRequest.of(page, 6));
        }

        model.addAttribute("items", productPage.getContent());
        model.addAttribute("hasMore", productPage.hasNext());

        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            model.addAttribute("user", user);
        }

        return "fragments/menu_items_fragment";
    }
}
