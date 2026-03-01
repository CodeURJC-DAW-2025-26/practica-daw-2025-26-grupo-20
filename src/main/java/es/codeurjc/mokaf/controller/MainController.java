package es.codeurjc.mokaf.controller;

import es.codeurjc.mokaf.model.Branch;
import es.codeurjc.mokaf.model.ContactRequest;
import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.service.BranchService;
import es.codeurjc.mokaf.service.ContactEmailService;
import es.codeurjc.mokaf.service.EmployeeService;
import es.codeurjc.mokaf.service.FaqService;
import es.codeurjc.mokaf.service.OrdersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
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
    private EmployeeService employeeService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private FaqService faqService;

    @Autowired
    private ContactEmailService contactEmailService;

    // ── Checkout ────────────────────────────────────────────────────────────

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

    // ── About Us ─────────────────────────────────────────────────────────────

    @GetMapping("/about_us")
    public String about_us(Model model) {
        model.addAttribute("team", employeeService.getEmployeesByDepartment("Atencion al cliente"));
        return "about_us";
    }

    // ── Branches ─────────────────────────────────────────────────────────────

    @GetMapping("/branches")
    public String branches(Model model) {
        List<Branch> branches = branchService.getAllBranches();
        model.addAttribute("branches", branches);
        return "branches";
    }

    // ── Contact ──────────────────────────────────────────────────────────────

    private void populateCommonModel(Model model) {
        model.addAttribute("title", "CONTACTANOS");
        model.addAttribute("currentPage", "contact");
        model.addAttribute("team", employeeService.getEmployeesByDepartment("Atención al cliente"));
        model.addAttribute("faqs", faqService.getAllFaqs());
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
            return "contact"; // return page with errors
        }

        // If valid, send the contact email
        contactEmailService.sendContactEmail(contactRequest);

        model.addAttribute("success", true);
        model.addAttribute("contactRequest", new ContactRequest());

        return "contact";
    }
}
