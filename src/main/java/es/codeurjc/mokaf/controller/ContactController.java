package es.codeurjc.mokaf.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import es.codeurjc.mokaf.model.ContactRequest;
import es.codeurjc.mokaf.repository.EmployeeRepository;
import es.codeurjc.mokaf.repository.FaqRepository;
import es.codeurjc.mokaf.service.ContactEmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class ContactController {

    private final EmployeeRepository employeeRepository;
    private final FaqRepository faqRepository;
    private final ContactEmailService contactEmailService;

    public ContactController(EmployeeRepository employeeRepository, FaqRepository faqRepository,
            ContactEmailService contactEmailService) {
        this.employeeRepository = employeeRepository;
        this.faqRepository = faqRepository;
        this.contactEmailService = contactEmailService;
    }

    private void populateCommonModel(Model model) {
        model.addAttribute("title", "CONTACTANOS");
        model.addAttribute("currentPage", "contact");
        model.addAttribute("team", employeeRepository.findByDepartment("Atención al cliente"));
        model.addAttribute("faqs", faqRepository.findAll());
    }

    @GetMapping("/contact")
    public String contact(HttpServletRequest request, Model model) {
        populateCommonModel(model);
        model.addAttribute("contactRequest", new ContactRequest());

        CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");
        if (csrf != null) {
            model.addAttribute("_csrf", csrf);
        }

        return "contact";
    }

    @PostMapping("/contact")
    public String submitContactForm(
            @Valid @ModelAttribute("contactRequest") ContactRequest contactRequest,
            BindingResult bindingResult,
            HttpServletRequest request,
            Model model) {

        populateCommonModel(model);

        CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");
        if (csrf != null) {
            model.addAttribute("_csrf", csrf);
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("formError", true);
            for (org.springframework.validation.FieldError error : bindingResult.getFieldErrors()) {
                model.addAttribute(error.getField() + "Error", error.getDefaultMessage());
            }
            return "contact"; // Retorna a la página con errores
        }

        // Si es válido, enviamos el email
        contactEmailService.sendContactEmail(contactRequest);

        // Agregamos bandera de éxito y creamos un form vacío
        model.addAttribute("success", true);
        model.addAttribute("contactRequest", new ContactRequest());

        return "contact";
    }
}
