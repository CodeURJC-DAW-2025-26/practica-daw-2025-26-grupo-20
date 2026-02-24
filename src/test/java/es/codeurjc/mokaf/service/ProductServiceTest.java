package es.codeurjc.mokaf.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.codeurjc.mokaf.model.Category;
import es.codeurjc.mokaf.model.Product;

class ProductServiceTest {

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService();
    }

    @Test
    void testGetAllProducts() {
        List<Product> products = productService.getAllProducts();
        assertNotNull(products);
        assertFalse(products.isEmpty());
        assertTrue(products.size() > 20); // There are multiple default seeded products
    }

    @Test
    void testGetProductById() {
        // Find existing product in default data
        List<Product> products = productService.getAllProducts();
        assertFalse(products.isEmpty());
        Product firstProduct = products.get(0);

        // Let's set an ID explicitly because the service might not assign it
        // automatically in the default constructor list.
        firstProduct.setId(99L);

        Product found = productService.getProductById(99L);
        assertNotNull(found);
        assertEquals("Expreso", found.getName());
    }

    @Test
    void testGetProductByIdNotFound() {
        Product found = productService.getProductById(999L);
        assertNull(found);
    }

    @Test
    void testAddProduct() {
        int initialSize = productService.getAllProducts().size();

        Product newProduct = new Product("New Drink", "Test drink", null, new BigDecimal("2.00"), Category.HOT);
        productService.addProduct(newProduct);

        List<Product> products = productService.getAllProducts();
        assertEquals(initialSize + 1, products.size());
        assertTrue(products.contains(newProduct));
    }

    @Test
    void testUpdateProduct() {
        // Setup product with auto-assigned ID
        Product product = new Product("Test Coffee", "Test", null, new BigDecimal("1.00"), Category.HOT);
        productService.addProduct(product);
        Long newId = product.getId();

        // Update product
        Product newProductData = new Product("Updated Coffee", "Updated", null, new BigDecimal("3.00"), Category.HOT);
        newProductData.setId(newId);

        productService.updateProduct(newId, newProductData);

        Product found = productService.getProductById(newId);
        assertNotNull(found);
        assertEquals("Updated Coffee", found.getName());
        assertEquals("Updated", found.getDescription());
        assertEquals(new BigDecimal("3.00"), found.getPriceBase());
    }

    @Test
    void testDeleteProduct() {
        // Setup product with auto-assigned ID
        Product product = new Product("To Be Deleted", "Test", null, new BigDecimal("1.00"), Category.HOT);
        productService.addProduct(product);
        Long newId = product.getId();

        int initialSize = productService.getAllProducts().size();

        productService.deleteProduct(newId);

        assertEquals(initialSize - 1, productService.getAllProducts().size());
        assertNull(productService.getProductById(newId));
    }
}
