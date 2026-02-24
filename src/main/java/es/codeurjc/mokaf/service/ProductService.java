package es.codeurjc.mokaf.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import es.codeurjc.mokaf.model.Product;
import es.codeurjc.mokaf.repository.OrderItemRepository;
import es.codeurjc.mokaf.repository.ProductRepository;

@Service("applicationProductService")
public class ProductService {

        private final ProductRepository productRepository;
        private final OrderItemRepository orderItemRepository;

        public ProductService(ProductRepository productRepository, OrderItemRepository orderItemRepository) {
                this.productRepository = productRepository;
                this.orderItemRepository = orderItemRepository;
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

        /**
         * Returns personalized recommendations for a logged-in user,
         * based on their most ordered products (PAID orders).
         * Falls back to best-sellers if the user has no history.
         */
        public List<Product> getRecommendedProducts(Long userId, int limit) {
                List<Product> recommended = orderItemRepository.findTopProductsByUserId(
                                userId, PageRequest.of(0, limit));

                // If user has no order history, fall back to best-sellers
                if (recommended.isEmpty()) {
                        recommended = getBestSellingProducts(limit);
                }

                return recommended;
        }

        /**
         * Returns the globally best-selling products (for anonymous users).
         * Falls back to the first N products if there are no sales yet.
         */
        public List<Product> getBestSellingProducts(int limit) {
                List<Product> bestSellers = orderItemRepository.findTopBestSellingProducts(
                                PageRequest.of(0, limit));

                // If there are no sales at all, return the first N products
                if (bestSellers.isEmpty()) {
                        bestSellers = productRepository.findAll()
                                        .stream()
                                        .limit(limit)
                                        .toList();
                }

                return bestSellers;
        }
}
