package es.codeurjc.mokaf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.mokaf.service.ProductService;

@Controller
public class menuController {

        @Autowired
        @Qualifier("applicationProductService")
        private ProductService productService;

        @GetMapping("/menu")
        public String showMenu(Model model) {
                model.addAttribute("title", "Menú");
                model.addAttribute("items", productService.getAllProducts()); // Añadimos la lista del servicio al
                                                                              // modelo
                model.addAttribute("currentPage", "menu");
                return "menu"; // Devuelve el nombre de la plantilla
        }

}
