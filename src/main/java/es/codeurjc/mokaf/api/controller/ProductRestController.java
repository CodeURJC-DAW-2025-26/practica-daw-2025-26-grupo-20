package es.codeurjc.mokaf.api.controller;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.mokaf.api.dto.ProductDTO;
import es.codeurjc.mokaf.api.dto.ProductDetailDTO;
import es.codeurjc.mokaf.api.mapper.ProductMapper;
import es.codeurjc.mokaf.model.Category;
import es.codeurjc.mokaf.model.Product;
import es.codeurjc.mokaf.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @GetMapping
    public Page<ProductDTO> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Product> products = productService.getProductsPage(page, size);
        return products.map(productMapper::toProductDTO);
    }

    @GetMapping("/{id}")
    public ProductDetailDTO getProduct(@PathVariable long id) {
        Product product = productService.findWithImageById(id)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado: " + id));

        return productMapper.toDTO(product);
    }

    @PostMapping
    public ProductDTO createProduct(@RequestBody ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.name());
        product.setDescription(productDTO.description());
        product.setPriceBase(productDTO.priceBase());

        try {
            if (productDTO.category() != null) {
                product.setCategory(Category.valueOf(productDTO.category()));
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Categoría inválida: " + productDTO.category());
        }

        productService.addProduct(product);

        return productMapper.toProductDTO(product);
    }

    @PutMapping("/{id}")
    public ProductDTO updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        Product product = productService.getProductById(id);

        if (product == null) {
            throw new NoSuchElementException("Producto no encontrado: " + id);
        }

        product.setName(productDTO.name());
        product.setDescription(productDTO.description());
        product.setPriceBase(productDTO.priceBase());

        try {
            if (productDTO.category() != null) {
                product.setCategory(Category.valueOf(productDTO.category()));
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Categoría inválida: " + productDTO.category());
        }

        productService.addProduct(product);

        return productMapper.toProductDTO(product);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        Product product = productService.getProductById(id);

        if (product == null) {
            throw new NoSuchElementException("Producto no encontrado: " + id);
        }

        productService.deleteProduct(id);
    }
}