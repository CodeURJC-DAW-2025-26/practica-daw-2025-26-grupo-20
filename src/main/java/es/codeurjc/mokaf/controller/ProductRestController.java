package es.codeurjc.mokaf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.mokaf.dto.ProductDetailDTO;
import es.codeurjc.mokaf.dto.ProductMapper;
import es.codeurjc.mokaf.model.Product;
import es.codeurjc.mokaf.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @GetMapping("/{id}")
    public ProductDetailDTO getProduct(@PathVariable long id) {
        Product product = productService.findWithImageById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));

        return productMapper.toDTO(product);
    }
}