package es.codeurjc.mokaf.api.controller;

import es.codeurjc.mokaf.api.dto.ProductDTO;
import es.codeurjc.mokaf.model.Product;
import es.codeurjc.mokaf.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/products")
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "Get all products paginated")
    @GetMapping
    public Page<ProductDTO> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Product> products = productService.getProductsPage(page, size);
        return products.map(ProductDTO::new);
    }

    @Operation(summary = "Get a product by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the product"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        Product p = productService.getProductById(id);
        return p != null ? ResponseEntity.ok(new ProductDTO(p)) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Create a new product")
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        // Here normally DTO would be converted to entity mapped. Simplification for
        // structure:
        Product p = new Product();
        p.setName(productDTO.getName());
        p.setDescription(productDTO.getDescription());
        p.setPriceBase(productDTO.getPriceBase());
        // Category should ideally be mapped through enum conversion
        try {
            if (productDTO.getCategory() != null) {
                p.setCategory(es.codeurjc.mokaf.model.Category.valueOf(productDTO.getCategory()));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        productService.addProduct(p);
        Product savedProduct = p;

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedProduct.getId()).toUri();

        return ResponseEntity.created(location).body(new ProductDTO(savedProduct));
    }

    @Operation(summary = "Update an existing product")
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        Product p = productService.getProductById(id);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }

        p.setName(productDTO.getName());
        p.setDescription(productDTO.getDescription());
        p.setPriceBase(productDTO.getPriceBase());
        try {
            if (productDTO.getCategory() != null) {
                p.setCategory(es.codeurjc.mokaf.model.Category.valueOf(productDTO.getCategory()));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        productService.addProduct(p); // addProduct also serves as update
        return ResponseEntity.ok(new ProductDTO(p));
    }

    @Operation(summary = "Delete a product")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productService.getProductById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
