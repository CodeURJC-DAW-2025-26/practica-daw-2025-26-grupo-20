package es.codeurjc.mokaf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class ContactController {

    @GetMapping("/contact")
    public String contact(Model model) {
    
        return "contact"; // Mustache: contact.mustache
    }
}
