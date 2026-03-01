package es.codeurjc.mokaf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "Mokaf - Specialty Coffee");
        model.addAttribute("currentPage", "home");
        return "index";
    }

    @GetMapping("/login")
    public String showLogin(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "registered", required = false) String registered,
            @RequestParam(value = "deleted", required = false) String deleted,
            Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "Incorrect email or password");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "You have logged out successfully");
        }
        if (registered != null) {
            model.addAttribute("registeredMessage", "Registration successful. You can now log in.");
        }
        if (deleted != null) {
            model.addAttribute("deletedMessage", "Your account has been deleted.");
        }

        model.addAttribute("title", "Login - Mokaf");
        model.addAttribute("currentPage", "login");
        return "login";
    }
}