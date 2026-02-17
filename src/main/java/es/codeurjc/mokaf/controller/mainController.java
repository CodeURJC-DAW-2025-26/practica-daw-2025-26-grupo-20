package es.codeurjc.mokaf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// import es.codeurjc.mokaf.mysql.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

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
    public String showContact(Model model) {
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

        // model.addAttribute("mapUrl", branchService.getMapUrl());
        // model.addAttribute("locationName", branchService.getLocationName());
        // model.addAttribute("locationMetro", branchService.getMetroInfo());
        // model.addAttribute("locationBus", branchService.getBusInfo());

        // Hours data
        // model.addAttribute("hours", branchService.getOpeningHours());

        // FAQ data
        // model.addAttribute("faqs", branchService.getFAQs());

        // Contact Info
        // model.addAttribute("contactPhone", branchService.getContactPhone());
        // model.addAttribute("contactEmail", branchService.getContactEmail());
        // model.addAttribute("contactSupport", branchService.getContactSupportHigh());

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
