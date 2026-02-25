package es.codeurjc.mokaf.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.mokaf.repository.EmployeeRepository;
import es.codeurjc.mokaf.repository.FaqRepository;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ContactController {

    private final EmployeeRepository employeeRepository;
    private final FaqRepository faqRepository;

    public ContactController(EmployeeRepository employeeRepository, FaqRepository faqRepository) {
        this.employeeRepository = employeeRepository;
        this.faqRepository = faqRepository;
    }

    @GetMapping("/contact")
    public String contact(HttpServletRequest request, Model model) {
        model.addAttribute("title", "CONTACTANOS");
        model.addAttribute("currentPage", "contact");

        // Fetch team from DB
        model.addAttribute("team", employeeRepository.findByDepartment("Atención al cliente"));

        // Fetch faqs from DB
        model.addAttribute("faqs", faqRepository.findAll());

        // Add CSRF token if present
        CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");
        if (csrf != null) {
            model.addAttribute("_csrf", csrf);
        }

        return "contact"; // Mustache: contact.mustache
    }
}
