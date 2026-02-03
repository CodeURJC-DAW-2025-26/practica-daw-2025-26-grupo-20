package es.codeurjc.mokaf.controller;

import es.codeurjc.mokaf.model.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class productController {

  @GetMapping("/product")
    public String product(Model model) {
        System.out.println(">>> ENTRANDO EN ProductController /product");
        model.addAttribute("title", "Producto · MokaF");
        model.addAttribute("product", new Product("Expreso","Café negro fuerte y aromático.","2.50€",
                "/images/MenuImages/Hot/Expreso.png","Hot"));
        model.addAttribute("currentPage", "product");
        return "product";
}

}
