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
import es.codeurjc.mokaf.model.Allergen;
import es.codeurjc.mokaf.service.ProductService;
import es.codeurjc.mokaf.service.AllergenService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Controller
public class GestionController {

    @Autowired
    @Qualifier("applicationProductService")
    private ProductService productService;

    @Autowired
    private AllergenService allergenService;

    @GetMapping("/admin/gestion_menu")
    public String showGestion(Model model, @RequestParam(required = false) String error) {
        model.addAttribute("title", "Gestión de Menú");
        model.addAttribute("items", productService.getAllProducts());
        model.addAttribute("allergens", allergenService.getAllAllergens());
        model.addAttribute("currentPage", "gestion");
        if (error != null) {
            model.addAttribute("error", error);
        }

        List<java.util.Map<String, String>> categories = new java.util.ArrayList<>();
        categories.add(java.util.Map.of("value", "HOT", "displayName", "Calientes"));
        categories.add(java.util.Map.of("value", "COLD", "displayName", "Fríos"));
        categories.add(java.util.Map.of("value", "BLENDED", "displayName", "Mezclados"));
        categories.add(java.util.Map.of("value", "DESSERTS", "displayName", "Postres"));
        categories.add(java.util.Map.of("value", "NON_COFFEE", "displayName", "Sin Café"));
        model.addAttribute("categories", categories);

        return "admin/gestion_menu";
    }

    @PostMapping("/admin/gestion_menu/add")
    public String addProduct(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String price,
            @RequestParam("image") org.springframework.web.multipart.MultipartFile imageFile,
            @RequestParam(required = false) List<Long> allergenIds,
            @RequestParam String category) throws Exception {

        // Validations
        if (name.trim().isEmpty() || description.trim().isEmpty() || category.trim().isEmpty()) {
            return "redirect:/admin/gestion_menu?error=Campos requeridos vacíos";
        }

        // Convert price to BigDecimal
        String priceValue = price.replace("€", "").trim();
        BigDecimal priceBase;
        try {
            priceBase = new BigDecimal(priceValue);
            if (priceBase.compareTo(BigDecimal.ZERO) < 0) {
                return "redirect:/admin/gestion_menu?error=El precio no puede ser negativo";
            }
        } catch (NumberFormatException e) {
            return "redirect:/admin/gestion_menu?error=Precio inválido";
        }

        // Convert category string to enum
        Category categoryEnum = Category.valueOf(category.toUpperCase());

        // Convert image file to Blob
        if (imageFile.isEmpty()) {
            return "redirect:/admin/gestion_menu?error=La imagen es obligatoria";
        }
        byte[] imageData = imageFile.getBytes();
        Image imageObj = new Image(new javax.sql.rowset.serial.SerialBlob(imageData));

        Product newProduct = new Product(name, description, imageObj, priceBase, categoryEnum);

        Set<Allergen> allergenSet = new HashSet<>();
        if (allergenIds != null) {
            for (Long aId : allergenIds) {
                allergenService.findById(aId).ifPresent(allergenSet::add);
            }
        }
        newProduct.setAllergens(allergenSet);

        productService.addProduct(newProduct);

        return "redirect:/admin/gestion_menu";
    }

    @PostMapping("/admin/gestion_menu/edit")
    public String editProduct(
            @RequestParam Long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String price,
            @RequestParam("image") org.springframework.web.multipart.MultipartFile imageFile,
            @RequestParam(required = false) List<Long> allergenIds,
            @RequestParam String category) throws Exception {

        // Validations
        if (name.trim().isEmpty() || description.trim().isEmpty() || category.trim().isEmpty()) {
            return "redirect:/admin/gestion_menu?error=Campos requeridos vacíos";
        }

        // Convert price to BigDecimal
        String priceValue = price.replace("€", "").trim();
        BigDecimal priceBase;
        try {
            priceBase = new BigDecimal(priceValue);
            if (priceBase.compareTo(BigDecimal.ZERO) < 0) {
                return "redirect:/admin/gestion_menu?error=El precio no puede ser negativo";
            }
        } catch (NumberFormatException e) {
            return "redirect:/admin/gestion_menu?error=Precio inválido";
        }

        // Convert category string to enum
        Category categoryEnum = Category.valueOf(category.toUpperCase());

        // Fetch existing product to preserve image if new one is empty
        Product existingProduct = productService.getProductById(id);
        if (existingProduct == null) {
            return "redirect:/admin/gestion_menu?error=Producto no encontrado";
        }
        Image imageObj = existingProduct.getImage();

        // Convert new image file to Blob if provided
        if (!imageFile.isEmpty()) {
            byte[] imageData = imageFile.getBytes();
            imageObj = new Image(new javax.sql.rowset.serial.SerialBlob(imageData));
        }

        // Update existing product fields
        existingProduct.setName(name);
        existingProduct.setDescription(description);
        existingProduct.setPriceBase(priceBase);
        existingProduct.setCategory(categoryEnum);
        existingProduct.setImage(imageObj);

        Set<Allergen> allergenSet = new HashSet<>();
        if (allergenIds != null) {
            for (Long aId : allergenIds) {
                allergenService.findById(aId).ifPresent(allergenSet::add);
            }
        }
        existingProduct.setAllergens(allergenSet);

        productService.updateProduct(id, existingProduct);

        return "redirect:/admin/gestion_menu";
    }

    @PostMapping("/admin/gestion_menu/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/gestion_menu";
    }
}
