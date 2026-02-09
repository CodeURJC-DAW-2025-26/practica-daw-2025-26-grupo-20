package es.codeurjc.mokaf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class mainController { // Controller for fragments

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


    @GetMapping("/statistics")
    public String showStat(Model model) {
        model.addAttribute("title", "Statistics");
        model.addAttribute("currentPage", "Statistics");
        return "statistics";
    }
  
    @GetMapping("/nosotros")
    public String showAboutUs(Model model) {
        model.addAttribute("title", "Sobre Nosotros - Mokaf");
        model.addAttribute("currentPage", "nosotros");
        return "nosotros";
    }
}
