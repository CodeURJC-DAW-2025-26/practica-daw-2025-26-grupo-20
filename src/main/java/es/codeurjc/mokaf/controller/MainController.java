package es.codeurjc.mokaf.controller;

import java.util.Arrays;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class MainController { // Controller for fragments

    // @Autowired
    // @Qualifier("mysqlBranchService")
    // private BranchService branchService;

    @GetMapping("/cart")
    public String showCart(Model model) {
        model.addAttribute("title", "Carrito de Compra - Mokaf");
        model.addAttribute("currentPage", "cart");
        return "cart";
    }

  
   
    @GetMapping("/contact")
    public String showContact(HttpServletRequest request, Model model) {
        model.addAttribute("title", "Contact Us");
        model.addAttribute("currentPage", "contact");

        // Location data
        // Location data from Service
        // var branch = branchService.getMainBranch();

        // Simple logic to extract city/country from description for now, or just send
        // strings
        // Based on "Calle del Café, 123, 28001 Madrid, España"
        // String fullAddress = branch.getDescription();
        String fullAddress = "Calle del Café, 123, 28001 Madrid, España"; // HARDCODED
        String[] addressParts = fullAddress.split(", ");

        if (addressParts.length >= 3) {
            model.addAttribute("locationAddress", addressParts[0]); // Calle del Café, 123
            model.addAttribute("locationCity", addressParts[1]); // 28001 Madrid (approx)
            model.addAttribute("locationCountry", addressParts[2]); // España
        } else {
            // Fallback
            model.addAttribute("locationAddress", "Calle del Café, 123");
            model.addAttribute("locationCity", "28001 Madrid");
            model.addAttribute("locationCountry", "España");
        }

        // Map URL
        model.addAttribute("mapUrl", "https://maps.google.com/?q=Calle+del+Café,+123,+28001+Madrid,+España");

        // model.addAttribute("locationName", branchService.getLocationName());
        // model.addAttribute("locationMetro", branchService.getMetroInfo());
        // model.addAttribute("locationBus", branchService.getBusInfo());

        // Hours data
        model.addAttribute("hours", Arrays.asList(
            "Lunes a Viernes: 08:00 - 20:00",
            "Sábados: 09:00 - 21:00",
            "Domingos: 10:00 - 18:00"
        ));

        // model.addAttribute("faqs", branchService.getFAQs());

        // Contact Info
        // model.addAttribute("contactPhone", branchService.getContactPhone());
        // model.addAttribute("contactEmail", branchService.getContactEmail());
        // model.addAttribute("contactSupport", branchService.getContactSupportHigh());

        // Add CSRF token
        CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");
        if (csrf != null) {
            model.addAttribute("_csrf", csrf);
        }

        return "contact";
    }

    @GetMapping("/statistics")
    public String showStat(Model model) {
        model.addAttribute("title", "Statistics");
        model.addAttribute("currentPage", "Statistics");
        return "statistics";
    }

}
