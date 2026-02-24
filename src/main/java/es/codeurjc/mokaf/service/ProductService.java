package es.codeurjc.mokaf.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import es.codeurjc.mokaf.model.Product;
import es.codeurjc.mokaf.repository.ProductRepository;

@Service("applicationProductService")
public class ProductService {

        private final ProductRepository productRepository;

        public ProductService(ProductRepository productRepository) {
                this.productRepository = productRepository;
        }

        public List<Product> getAllProducts() {
                return productRepository.findAll();
        }

        public void addProduct(Product product) {
                productRepository.save(product);
        }

        public Product getProductById(Long id) {
                Optional<Product> product = productRepository.findById(id);
                return product.orElse(null);
        }

        public void updateProduct(Long id, Product newProduct) {
                if (productRepository.existsById(id)) {
                        newProduct.setId(id);
                        productRepository.save(newProduct);
                }
        }

        public void deleteProduct(Long id) {
                productRepository.deleteById(id);
        }
}
