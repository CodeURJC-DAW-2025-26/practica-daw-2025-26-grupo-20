package es.codeurjc.mokaf.controller;

import es.codeurjc.mokaf.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

    private final ProductRepository productRepository;

    public MenuController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/menu")
    public String showMenu(Model model) {
        model.addAttribute("title", "Menú");
        model.addAttribute("items", productRepository.findAll());
        model.addAttribute("currentPage", "menu");
        return "menu";
    }
}
