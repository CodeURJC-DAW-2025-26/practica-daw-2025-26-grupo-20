package es.codeurjc.mokaf.controller;

import es.codeurjc.mokaf.model.Category;
import es.codeurjc.mokaf.model.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

@Controller
public class ProductController {

  @GetMapping("/product/{id}")
    public String product(@PathVariable Long id, Model model) {
        System.out.println(">>> ENTRANDO EN ProductController /product/" + id);
        
        Product product = new Product(id,
            "Expreso", 
            "black cofee strong and aromatic.", 
            "2.50€",
            "/images/MenuImages/Hot/Expreso.png", 
            "Hot"
        );
        
        model.addAttribute("title", "Producto · MokaF");
        model.addAttribute("product", product);
        model.addAttribute("currentPage", "product");
        
        return "product";
    }

}
