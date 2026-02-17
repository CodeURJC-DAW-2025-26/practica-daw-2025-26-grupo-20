package es.codeurjc.mokaf.mysql.controller;

import es.codeurjc.mokaf.mysql.model.Product;
import es.codeurjc.mokaf.mysql.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("mysqlProductController")
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.findById(id).orElse(null);
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.save(product);
    }
}
