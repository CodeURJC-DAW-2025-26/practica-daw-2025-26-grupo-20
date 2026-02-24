package es.codeurjc.mokaf.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import es.codeurjc.mokaf.model.Category;
import es.codeurjc.mokaf.model.Product;
import es.codeurjc.mokaf.repository.OrderItemRepository;
import es.codeurjc.mokaf.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product("Expreso", "Café negro fuerte y aromático.", null,
                new BigDecimal("2.50"), Category.HOT);
        sampleProduct.setId(1L);
    }

    @Test
    void testGetAllProducts() {
        List<Product> mockProducts = new ArrayList<>();
        mockProducts.add(sampleProduct);
        mockProducts.add(new Product("Capuccino", "Expreso con leche vaporizada y espuma.", null,
                new BigDecimal("3.50"), Category.HOT));

        when(productRepository.findAll()).thenReturn(mockProducts);

        List<Product> products = productService.getAllProducts();

        assertNotNull(products);
        assertEquals(2, products.size());
        assertEquals("Expreso", products.get(0).getName());
        verify(productRepository).findAll();
    }

    @Test
    void testGetProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        Product found = productService.getProductById(1L);

        assertNotNull(found);
        assertEquals("Expreso", found.getName());
        assertEquals(new BigDecimal("2.50"), found.getPriceBase());
        verify(productRepository).findById(1L);
    }

    @Test
    void testGetProductByIdNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        Product found = productService.getProductById(999L);

        assertNull(found);
        verify(productRepository).findById(999L);
    }

    @Test
    void testAddProduct() {
        Product newProduct = new Product("New Drink", "Test drink", null,
                new BigDecimal("2.00"), Category.HOT);

        when(productRepository.save(newProduct)).thenReturn(newProduct);

        productService.addProduct(newProduct);

        verify(productRepository).save(newProduct);
    }

    @Test
    void testUpdateProduct() {
        Product updatedProduct = new Product("Updated Coffee", "Updated", null,
                new BigDecimal("3.00"), Category.HOT);

        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        productService.updateProduct(1L, updatedProduct);

        assertEquals(1L, updatedProduct.getId());
        verify(productRepository).existsById(1L);
        verify(productRepository).save(updatedProduct);
    }

    @Test
    void testUpdateProductNotFound() {
        Product updatedProduct = new Product("Updated Coffee", "Updated", null,
                new BigDecimal("3.00"), Category.HOT);

        when(productRepository.existsById(999L)).thenReturn(false);

        productService.updateProduct(999L, updatedProduct);

        verify(productRepository).existsById(999L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void testDeleteProduct() {
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }

    // ===== Recommendation Tests =====

    @Test
    void testGetRecommendedProductsForUser() {
        List<Product> userFavorites = List.of(sampleProduct);
        when(orderItemRepository.findTopProductsByUserId(eq(1L), any(PageRequest.class)))
                .thenReturn(userFavorites);

        List<Product> result = productService.getRecommendedProducts(1L, 4);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Expreso", result.get(0).getName());
        verify(orderItemRepository).findTopProductsByUserId(eq(1L), any(PageRequest.class));
    }

    @Test
    void testGetRecommendedProductsFallbackToBestSellers() {
        // User has no order history
        when(orderItemRepository.findTopProductsByUserId(eq(1L), any(PageRequest.class)))
                .thenReturn(Collections.emptyList());

        List<Product> bestSellers = List.of(sampleProduct);
        when(orderItemRepository.findTopBestSellingProducts(any(PageRequest.class)))
                .thenReturn(bestSellers);

        List<Product> result = productService.getRecommendedProducts(1L, 4);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderItemRepository).findTopBestSellingProducts(any(PageRequest.class));
    }

    @Test
    void testGetBestSellingProducts() {
        List<Product> bestSellers = List.of(sampleProduct);
        when(orderItemRepository.findTopBestSellingProducts(any(PageRequest.class)))
                .thenReturn(bestSellers);

        List<Product> result = productService.getBestSellingProducts(4);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Expreso", result.get(0).getName());
    }

    @Test
    void testGetBestSellingProductsFallbackToAllProducts() {
        // No sales yet
        when(orderItemRepository.findTopBestSellingProducts(any(PageRequest.class)))
                .thenReturn(Collections.emptyList());
        when(productRepository.findAll()).thenReturn(List.of(sampleProduct));

        List<Product> result = productService.getBestSellingProducts(4);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository).findAll();
    }
}
