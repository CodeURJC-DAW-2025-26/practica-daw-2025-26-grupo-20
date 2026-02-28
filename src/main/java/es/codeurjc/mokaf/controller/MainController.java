package es.codeurjc.mokaf.controller;

import es.codeurjc.mokaf.model.Branch;
import es.codeurjc.mokaf.model.ContactRequest;
import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.repository.BranchRepository;
import es.codeurjc.mokaf.repository.EmployeeRepository;
import es.codeurjc.mokaf.service.OrdersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.PostMapping;
import es.codeurjc.mokaf.model.ContactRequest;
import es.codeurjc.mokaf.repository.EmployeeRepository;
import es.codeurjc.mokaf.repository.FaqRepository;
import es.codeurjc.mokaf.service.ContactEmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {


    @Autowired
    private OrdersService ordersService;

    @Autowired
    private EmployeeRepository employeeRepository;


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

    //Us

     @GetMapping("/nosotros")
    public String nosotros(Model model) {
        // Fetch team from DB
        model.addAttribute("team", employeeRepository.findByDepartment("Atencion al cliente"));
        return "about_us";
    }

    //Branches

    @Autowired
    private BranchRepository branchRepository;
    
     @GetMapping("/branches")
    public String branches ( Model model) {

        List<Branch> branches = branchRepository.findAll();
        model.addAttribute("branches", branches);
       
        return "branches";
    }


    //Contact

    @Autowired
    private FaqRepository faqRepository;
    
    @Autowired
    private ContactEmailService contactEmailService;

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
