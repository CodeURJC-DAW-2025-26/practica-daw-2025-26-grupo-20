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
import es.codeurjc.mokaf.model.Category;
import es.codeurjc.mokaf.model.Image;
import es.codeurjc.mokaf.service.ProductService;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.sql.rowset.serial.SerialBlob;

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
            @RequestParam String category) throws Exception {

        // Convert price to BigDecimal
        String priceValue = price.replace("€", "").trim();
        BigDecimal priceBase = new BigDecimal(priceValue);

        // Convert category string to enum
        Category categoryEnum = Category.valueOf(category.toUpperCase());

        // Convert image file to Blob
        byte[] imageData = Files.readAllBytes(Paths.get(image));
        Image imageObj = new Image(new SerialBlob(imageData));

        Product newProduct = new Product(name, description, imageObj, priceBase, categoryEnum);
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
            @RequestParam String category) throws Exception {

        // Convert price to BigDecimal
        String priceValue = price.replace("€", "").trim();
        BigDecimal priceBase = new BigDecimal(priceValue);

        // Convert category string to enum
        Category categoryEnum = Category.valueOf(category.toUpperCase());

        // Convert image file to Blob
        byte[] imageData = Files.readAllBytes(Paths.get(image));
        Image imageObj = new Image(new SerialBlob(imageData));

        Product updatedProduct = new Product(name, description, imageObj, priceBase, categoryEnum);
        productService.updateProduct(id, updatedProduct);

        return "redirect:/gestion_menu";
    }

    @PostMapping("/gestion_menu/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/gestion_menu";
    }
}
