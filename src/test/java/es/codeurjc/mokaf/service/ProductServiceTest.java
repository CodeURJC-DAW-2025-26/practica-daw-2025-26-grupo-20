package es.codeurjc.mokaf.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.codeurjc.mokaf.model.Category;
import es.codeurjc.mokaf.model.Product;
import es.codeurjc.mokaf.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

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
}
