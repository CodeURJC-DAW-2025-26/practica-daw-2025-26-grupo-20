package es.codeurjc.mokaf.controller;

import es.codeurjc.mokaf.model.Category;
import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.model.Product;
import es.codeurjc.mokaf.service.AllergenService;
import es.codeurjc.mokaf.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MenuController {

    @Autowired
    private ProductService productService;
    @Autowired
    private AllergenService allergenService;

    @GetMapping("/menu")
    public String showMenu(Model model, Authentication authentication,
            @RequestParam(required = false) String category) {
        model.addAttribute("title", "Menú");

        Page<Product> productPage;
        if (category != null && !category.isEmpty() && !category.equalsIgnoreCase("all")) {
            try {
                Category cat = Category.valueOf(category.toUpperCase());
                productPage = productService.getProductsByCategoryPage(cat, 0, 6);
                model.addAttribute("selectedCategory", category);
            } catch (IllegalArgumentException e) {
                productPage = productService.getProductsPage(0, 6);
            }
        } else {
            productPage = productService.getProductsPage(0, 6);
        }

        model.addAttribute("items", productPage.getContent());
        model.addAttribute("hasMore", productPage.hasNext());

        model.addAttribute("categories", Category.values());
        model.addAttribute("allergens", allergenService.getAllAllergens());
        model.addAttribute("currentPage", "menu");

        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            boolean isAdmin = user.getRole() == User.Role.ADMIN;
            model.addAttribute("user", user);
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("isLogged", true);

            int recommendedLimit = 4;
            model.addAttribute("recommendedItems",
                    productService.getRecommendedProducts(user.getId(), recommendedLimit));
            model.addAttribute("recommendedTitle", "Recomendados para ti");
            model.addAttribute("recommendedSubtitle", "Basados en tus últimos pedidos");
        } else {
            int recommendedLimit = 4;
            model.addAttribute("recommendedItems",
                    productService.getBestSellingProducts(recommendedLimit));
            model.addAttribute("recommendedTitle", "Los más vendidos");
            model.addAttribute("recommendedSubtitle", "Basados en las preferencias de nuestros clientes");
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
                productPage = productService.getProductsByCategoryPage(cat, page, 6);
            } catch (IllegalArgumentException e) {
                productPage = productService.getProductsPage(page, 6);
            }
        } else {
            productPage = productService.getProductsPage(page, 6);
        }

        model.addAttribute("items", productPage.getContent());
        model.addAttribute("hasMore", productPage.hasNext());

        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            model.addAttribute("user", user);
            model.addAttribute("isLogged", true);
        }

        return "fragments/menu_items_fragment";
    }
}
