package es.codeurjc.mokaf.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import es.codeurjc.mokaf.model.Product;
import es.codeurjc.mokaf.model.Category;

@Service("applicationProductService")
public class ProductService {

        private List<Product> products = new ArrayList<>();
        private Long nextId = 1L;

        public ProductService() {
                // Initialize with default products
                // --- Hot ---
                addProduct(new Product("Expreso", "Café negro fuerte y aromático.", null, new BigDecimal("2.50"),
                                Category.HOT));
                addProduct(new Product("Capuccino", "Expreso con leche vaporizada y espuma.", null,
                                new BigDecimal("3.50"), Category.HOT));
                addProduct(new Product("Americano", "Expreso diluido con agua caliente.", null, new BigDecimal("2.80"),
                                Category.HOT));
                addProduct(new Product("Latte", "Expreso con una generosa cantidad de leche vaporizada.", null,
                                new BigDecimal("3.20"), Category.HOT));

                // --- Cold ---
                addProduct(new Product("Iced Latte", "Expreso y leche fría sobre hielo.", null, new BigDecimal("4.00"),
                                Category.COLD));
                addProduct(new Product("Frappe", "Café batido con hielo, refrescante y cremoso.", null,
                                new BigDecimal("4.20"), Category.COLD));
                addProduct(new Product("Iced Americano", "Expreso y agua fría servido sobre hielo.", null,
                                new BigDecimal("3.00"), Category.COLD));
                addProduct(new Product("Iced Vietnamese Coffe", "Café con leche condensada y hielo.", null,
                                new BigDecimal("4.50"), Category.COLD));

                // --- Blended ---
                addProduct(new Product("Frapuccino", "Bebida de café mezclada con hielo y sabores.", null,
                                new BigDecimal("4.50"), Category.BLENDED));
                addProduct(new Product("Chocolate Coffee Blend", "Mezcla de café y chocolate, batido con hielo.", null,
                                new BigDecimal("4.80"), Category.BLENDED));
                addProduct(new Product("Hazelnut Coffee Shake", "Batido de café con sirope de avellana.", null,
                                new BigDecimal("4.80"), Category.BLENDED));
                addProduct(new Product("Vanilla Frappe", "Frappé suave con un toque de vainilla.", null,
                                new BigDecimal("4.60"), Category.BLENDED));

                // --- Desserts ---
                addProduct(new Product("Croissants", "Clásico hojaldre francés.", null, new BigDecimal("2.00"),
                                Category.DESSERTS));
                addProduct(new Product("Chocolate Carrot Cake", "Pastel de zanahoria con cobertura de chocolate.", null,
                                new BigDecimal("3.50"), Category.DESSERTS));
                addProduct(new Product("Chocolate Cupcake", "Muffin de chocolate con frosting.", null,
                                new BigDecimal("2.80"), Category.DESSERTS));
                addProduct(new Product("Chocolate Green Tea Cupcake", "Muffin de té verde con corazón de chocolate.",
                                null, new BigDecimal("3.00"), Category.DESSERTS));
                addProduct(new Product("Dulce De Leche Desserts", "Postre cremoso de dulce de leche.", null,
                                new BigDecimal("3.20"), Category.DESSERTS));
                addProduct(new Product("Orange Cake", "Bizcocho esponjoso con sabor a naranja.", null,
                                new BigDecimal("3.50"), Category.DESSERTS));
                addProduct(new Product("Red Velvet Cupcake", "Clásico muffin Red Velvet con frosting de queso.", null,
                                new BigDecimal("3.00"), Category.DESSERTS));
                addProduct(new Product("Strawberry Cake", "Pastel de fresas con nata.", null, new BigDecimal("3.80"),
                                Category.DESSERTS));
                addProduct(new Product("Vanilla Cupcake", "Muffin de vainilla con frosting.", null,
                                new BigDecimal("2.80"), Category.DESSERTS));

                // --- Non-Coffee ---
                addProduct(new Product("Herbal Tea", "Infusión relajante sin cafeína.", null, new BigDecimal("3.00"),
                                Category.NON_COFFEE));
                addProduct(new Product("Chai Tea Latte", "Té negro especiado con leche vaporizada.", null,
                                new BigDecimal("3.80"), Category.NON_COFFEE));
                addProduct(new Product("Golden Milk", "Leche con cúrcuma y especias.", null, new BigDecimal("4.00"),
                                Category.NON_COFFEE));
                addProduct(new Product("Hot Chocolate", "Chocolate caliente espeso y cremoso.", null,
                                new BigDecimal("3.50"), Category.NON_COFFEE));
                addProduct(new Product("Matcha Latte", "Té verde matcha con leche vaporizada.", null,
                                new BigDecimal("4.20"), Category.NON_COFFEE));
        }

        public List<Product> getAllProducts() {
                return products;
        }

        public void addProduct(Product product) {
                if (product.getId() == null) {
                        product.setId(nextId++);
                }
                products.add(product);
        }

        public Product getProductById(Long id) {
                return products.stream()
                                .filter(p -> p.getId().equals(id))
                                .findFirst()
                                .orElse(null);
        }

        public void updateProduct(Long id, Product newProduct) {
                for (int i = 0; i < products.size(); i++) {
                        if (products.get(i).getId().equals(id)) {
                                products.set(i, newProduct);
                                break;
                        }
                }
        }

        public void deleteProduct(Long id) {
                products.removeIf(p -> p.getId().equals(id));
        }
}
