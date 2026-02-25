package es.codeurjc.mokaf.controller;

import es.codeurjc.mokaf.model.Category;
import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.repository.ProductRepository;
import es.codeurjc.mokaf.repository.AllergenRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
    public String showMenu(Model model, Authentication authentication) {
        model.addAttribute("title", "Menú");
        model.addAttribute("items", productRepository.findAll());
        model.addAttribute("categories", Category.values());
        model.addAttribute("allergens", allergenRepository.findAll());
        model.addAttribute("currentPage", "menu");

        if (authentication != null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();

            boolean isAdmin = user.getRole() == User.Role.ADMIN;
            model.addAttribute("user", user);
            model.addAttribute("isAdmin", isAdmin);

        }

        return "menu";
    }
}
