package es.codeurjc.mokaf.controller;

import java.util.Arrays;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ContactController {

    @GetMapping("/contact")
    public String contact(HttpServletRequest request, Model model) {
        model.addAttribute("title", "CONTACTANOS");
        model.addAttribute("currentPage", "contact");

        // Location (hardcoded for now)
        String fullAddress = "Calle del Café, 123, 28001 Madrid, España";
        String[] addressParts = fullAddress.split(", ");

        if (addressParts.length >= 3) {
            model.addAttribute("locationAddress", addressParts[0]);
            model.addAttribute("locationCity", addressParts[1]);
            model.addAttribute("locationCountry", addressParts[2]);
        } else {
            model.addAttribute("locationAddress", "Calle del Café, 123");
            model.addAttribute("locationCity", "28001 Madrid");
            model.addAttribute("locationCountry", "España");
        }

        model.addAttribute("mapUrl", "https://maps.google.com/?q=Calle+del+Café,+123,+28001+Madrid,+España");

        model.addAttribute("hours", Arrays.asList(
            "Lunes a Viernes: 08:00 - 20:00",
            "Sábados: 09:00 - 21:00",
            "Domingos: 10:00 - 18:00"
        ));

        // Add CSRF token if present
        CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");
        if (csrf != null) {
            model.addAttribute("_csrf", csrf);
        }

        return "contact"; // Mustache: contact.mustache
    }
}
