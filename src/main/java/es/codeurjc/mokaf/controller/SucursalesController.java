package es.codeurjc.mokaf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ch.qos.logback.core.model.Model;

@Controller
public class SucursalesController {
    
     @GetMapping("/sucursales")
    public String sucursales ( Model model) {

       
        return "sucursales";
    }
}
