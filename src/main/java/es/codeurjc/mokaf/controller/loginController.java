package es.codeurjc.mokaf.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.mokaf.model.Product;

@Controller
public class loginController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "Mokaf - Café de Especialidad");
        model.addAttribute("currentPage", "home");
        return "index";
    }


    @GetMapping("/login")
    public String showLogin(Model model) {
        model.addAttribute("title", "Login");
        model.addAttribute("currentPage", "login");
        return "login";
    }

}