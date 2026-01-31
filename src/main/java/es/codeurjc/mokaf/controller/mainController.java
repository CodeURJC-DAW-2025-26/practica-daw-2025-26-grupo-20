package es.codeurjc.mokaf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class mainController { //Controller for fragments
    
    @GetMapping("/cart")
    public String showCart(Model model) {
        model.addAttribute("title", "Carrito de Compra - Mokaf");
        model.addAttribute("currentPage", "cart");
        return "cart";
    }

        @GetMapping("/contact")
    public String showContact(Model model) {
        model.addAttribute("title", "Contact Us");
        model.addAttribute("currentPage", "contact");
        return "contact";
    }
}
