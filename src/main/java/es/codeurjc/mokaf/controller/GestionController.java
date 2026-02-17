package es.codeurjc.mokaf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.mokaf.model.Product;
import es.codeurjc.mokaf.service.ProductService;

@Controller
public class GestionController {

    @Autowired
    @Qualifier("applicationProductService")
    private ProductService productService;

    @GetMapping("/gestion_menu")
    public String showGestion(Model model) {
        model.addAttribute("title", "Gestión de Menú");
        model.addAttribute("items", productService.getAllProducts());
        model.addAttribute("currentPage", "gestion");
        return "gestion_menu";
    }

    @PostMapping("/gestion_menu/add")
    public String addProduct(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String price,
            @RequestParam String image,
            @RequestParam String category) {

        // Ensure price has symbol
        String priceWithSymbol = price.contains("€") ? price : price + "€";

        Product newProduct = new Product(name, description, priceWithSymbol, image, category);
        productService.addProduct(newProduct);

        return "redirect:/gestion_menu";
    }

    @PostMapping("/gestion_menu/edit")
    public String editProduct(
            @RequestParam Long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String price,
            @RequestParam String image,
            @RequestParam String category) {

        // Ensure price has symbol
        String priceWithSymbol = price.contains("€") ? price : price + "€";

        Product updatedProduct = new Product(name, description, priceWithSymbol, image, category);
        productService.updateProduct(id, updatedProduct);

        return "redirect:/gestion_menu";
    }

    @PostMapping("/gestion_menu/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/gestion_menu";
    }
}
