package es.codeurjc.mokaf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController { // Controller for fragments

    // @Autowired
    // @Qualifier("mysqlBranchService")
    // private BranchService branchService;

    @GetMapping("/cart")
    public String showCart(Model model) {
        model.addAttribute("title", "Carrito de Compra - Mokaf");
        model.addAttribute("currentPage", "cart");
        model.addAttribute("subtotal", "0.00€");
        model.addAttribute("shipping", "0.00€");
        model.addAttribute("tax", "0.00€");
        model.addAttribute("total", "0.00€");
        return "cart";
    }

    // Contact handled by ContactController

}
