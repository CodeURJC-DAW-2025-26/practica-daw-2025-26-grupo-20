package es.codeurjc.mokaf.api.controller;

import es.codeurjc.mokaf.api.dto.ProductDTO;
import es.codeurjc.mokaf.api.mapper.ProductMapper;
import es.codeurjc.mokaf.model.Product;
import es.codeurjc.mokaf.service.ProductService;
import es.codeurjc.mokaf.api.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @Operation(summary = "Get all products paginated")
    @GetMapping
    public Page<ProductDTO> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Product> products = productService.getProductsPage(page, size);
        return products.map(productMapper::toDTO);
    }

    @Operation(summary = "Get a product by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the product"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ProductDTO getProduct(@PathVariable Long id) {
        Product p = productService.getProductById(id);
        if (p == null) {
            throw new ResourceNotFoundException("Product with id " + id + " not found");
        }
        return productMapper.toDTO(p);
    }

    @Operation(summary = "Create a new product")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createProduct(@Valid @RequestBody ProductDTO productDTO) {
        Product p = productMapper.toEntity(productDTO);
        productService.addProduct(p);
        return productMapper.toDTO(p);
    }

    @Operation(summary = "Update an existing product")
    @PutMapping("/{id}")
    public ProductDTO updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        Product p = productService.getProductById(id);
        if (p == null) {
            throw new ResourceNotFoundException("Product with id " + id + " not found");
        }

        productMapper.updateEntity(p, productDTO);
        productService.addProduct(p);
        return productMapper.toDTO(p);
    }

    @Operation(summary = "Delete a product")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        if (productService.getProductById(id) == null) {
            throw new ResourceNotFoundException("Product with id " + id + " not found");
        }
        productService.deleteProduct(id);
    }
}
