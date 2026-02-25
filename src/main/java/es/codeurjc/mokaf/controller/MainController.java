package es.codeurjc.mokaf.controller;

import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.service.OrdersService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {

    private final OrdersService ordersService;

    public MainController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @PostMapping("/cart/checkout")
    public String checkout(Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            User user = (User) principal;
            boolean success = ordersService.processCheckout(user.getId());
            if (success) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Pedido completado con éxito. Se te ha enviado la factura al correo.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "No se pudo procesar tu pedido. Tu carrito está vacío.");
            }
        }

        return "redirect:/orders";
    }

}
