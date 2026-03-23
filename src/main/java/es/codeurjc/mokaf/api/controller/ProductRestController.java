package es.codeurjc.mokaf.api.controller;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.mokaf.api.dto.ProductDTO;
import es.codeurjc.mokaf.api.dto.ProductDetailDTO;
import es.codeurjc.mokaf.api.dto.ReviewDTO;
import es.codeurjc.mokaf.api.mapper.ProductMapper;
import es.codeurjc.mokaf.api.mapper.ReviewMapper;
import es.codeurjc.mokaf.model.Category;
import es.codeurjc.mokaf.model.Image;
import es.codeurjc.mokaf.model.Product;
import es.codeurjc.mokaf.model.Review;
import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.service.ProductService;
import es.codeurjc.mokaf.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/products")
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewMapper reviewMapper;

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

    @GetMapping("/{id}/reviews")
    public Page<ReviewDTO> getProductReviews(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Review> reviewPage = reviewService.getReviewsPage(id, page, size);
        return reviewPage.map(reviewMapper::toDTO);
    }

    @PostMapping("/{id}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDTO createReview(
            @PathVariable Long id,
            @RequestBody ReviewDTO reviewDTO,
            @AuthenticationPrincipal User currentUser) {

        Review newReview = reviewService.createReview(id, currentUser, reviewDTO.stars(), reviewDTO.text());
        return reviewMapper.toDTO(newReview);
    }

    @DeleteMapping("/{productId}/reviews/{reviewId}")
    public ResponseEntity<java.util.Map<String, String>> deleteReview(
            @PathVariable Long productId,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal User currentUser) {

        try {
            reviewService.deleteReview(productId, reviewId, currentUser);
            return ResponseEntity.ok(java.util.Map.of("message", "Review deleted successfully"));
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(java.util.Map.of("message", "Cannot delete review: constraint violation"));
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDTO> createProduct(@ModelAttribute ProductDTO productDTO) {
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

        if (productDTO.imageFile() != null && !productDTO.imageFile().isEmpty()) {
            try {
                byte[] imageData = productDTO.imageFile().getBytes();
                Image imageObj = new Image(new javax.sql.rowset.serial.SerialBlob(imageData));
                product.setImage(imageObj);
            } catch (Exception e) {
                throw new RuntimeException("Error guardando la imagen", e);
            }
        }

        productService.addProduct(product);
        ProductDTO savedDto = productMapper.toProductDTO(product);

        return ResponseEntity.created(
                ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(product.getId())
                    .toUri())
            .body(savedDto);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductDTO updateProduct(@PathVariable Long id, @ModelAttribute ProductDTO productDTO) {
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

        if (productDTO.imageFile() != null && !productDTO.imageFile().isEmpty()) {
            try {
                byte[] imageData = productDTO.imageFile().getBytes();
                Image imageObj = new Image(new javax.sql.rowset.serial.SerialBlob(imageData));
                product.setImage(imageObj);
            } catch (Exception e) {
                throw new RuntimeException("Error guardando la imagen", e);
            }
        }

        productService.addProduct(product);

        return productMapper.toProductDTO(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<java.util.Map<String, String>> deleteProduct(@PathVariable Long id) {
        Product product = productService.getProductById(id);

        if (product == null) {
            throw new NoSuchElementException("Producto no encontrado: " + id);
        }

        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(java.util.Map.of("message", "Product deleted successfully"));
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(java.util.Map.of("message", "Cannot delete product: it is referenced by other records"));
        }
    }
}