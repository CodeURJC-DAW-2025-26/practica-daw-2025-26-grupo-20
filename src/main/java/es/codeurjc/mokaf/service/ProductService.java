package es.codeurjc.mokaf.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;
import es.codeurjc.mokaf.model.Product;

@Service("applicationProductService")
public class ProductService {

        private List<Product> products = new ArrayList<>();
        private AtomicLong nextId = new AtomicLong(1);

        public ProductService() {
                // Initialize with default products
                // --- Hot ---
                products.add(new Product(nextId.getAndIncrement(), "Expreso", "Café negro fuerte y aromático.", "2.50€",
                                "/images/MenuImages/Hot/Expreso.png", "Hot"));
                products.add(new Product(nextId.getAndIncrement(), "Capuccino",
                                "Expreso con leche vaporizada y espuma.", "3.50€",
                                "/images/MenuImages/Hot/Capuccino.png", "Hot"));
                products.add(new Product(nextId.getAndIncrement(), "Americano", "Expreso diluido con agua caliente.",
                                "2.80€", "/images/MenuImages/Hot/Americano.png", "Hot"));
                products.add(new Product(nextId.getAndIncrement(), "Latte",
                                "Expreso con una generosa cantidad de leche vaporizada.", "3.20€",
                                "/images/MenuImages/Hot/Latte.png", "Hot"));

                // --- Cold ---
                products.add(new Product(nextId.getAndIncrement(), "Iced Latte", "Expreso y leche fría sobre hielo.",
                                "4.00€", "/images/MenuImages/Cold/IcedLatte.png", "Cold"));
                products.add(new Product(nextId.getAndIncrement(), "Frappe",
                                "Café batido con hielo, refrescante y cremoso.", "4.20€",
                                "/images/MenuImages/Cold/Frappe.png", "Cold"));
                products.add(new Product(nextId.getAndIncrement(), "Iced Americano",
                                "Expreso y agua fría servido sobre hielo.", "3.00€",
                                "/images/MenuImages/Cold/IcedAmericano.png", "Cold"));
                products.add(new Product(nextId.getAndIncrement(), "Iced Vietnamese Coffe",
                                "Café con leche condensada y hielo.", "4.50€",
                                "/images/MenuImages/Cold/IcedVietnameseCoffe.png", "Cold"));

                // --- Blended ---
                products.add(new Product(nextId.getAndIncrement(), "Frapuccino",
                                "Bebida de café mezclada con hielo y sabores.", "4.50€",
                                "/images/MenuImages/Blended/Frapuccino.png", "Blended"));
                products.add(new Product(nextId.getAndIncrement(), "Chocolate Coffee Blend",
                                "Mezcla de café y chocolate, batido con hielo.", "4.80€",
                                "/images/MenuImages/Blended/ChocolateCoffeeBlend.png", "Blended"));
                products.add(new Product(nextId.getAndIncrement(), "Hazelnut Coffee Shake",
                                "Batido de café con sirope de avellana.", "4.80€",
                                "/images/MenuImages/Blended/HazelnutCoffeeShake.png", "Blended"));
                products.add(new Product(nextId.getAndIncrement(), "Vanilla Frappe",
                                "Frappé suave con un toque de vainilla.", "4.60€",
                                "/images/MenuImages/Blended/VanillaFrappe.png", "Blended"));

                // --- Desserts ---
                products.add(new Product(nextId.getAndIncrement(), "Croissants", "Clásico hojaldre francés.", "2.00€",
                                "/images/MenuImages/Desserts/Croisants.png", "Desserts"));
                products.add(new Product(nextId.getAndIncrement(), "Chocolate Carrot Cake",
                                "Pastel de zanahoria con cobertura de chocolate.", "3.50€",
                                "/images/MenuImages/Desserts/ChocolateCarrotCake.png", "Desserts"));
                products.add(new Product(nextId.getAndIncrement(), "Chocolate Cupcake",
                                "Muffin de chocolate con frosting.", "2.80€",
                                "/images/MenuImages/Desserts/ChocolateCupcake.png", "Desserts"));
                products.add(new Product(nextId.getAndIncrement(), "Chocolate Green Tea Cupcake",
                                "Muffin de té verde con corazón de chocolate.", "3.00€",
                                "/images/MenuImages/Desserts/ChocolateGreenTeaCupcake.png", "Desserts"));
                products.add(new Product(nextId.getAndIncrement(), "Dulce De Leche Desserts",
                                "Postre cremoso de dulce de leche.", "3.20€",
                                "/images/MenuImages/Desserts/DulceDeLecheDesserts.png", "Desserts"));
                products.add(new Product(nextId.getAndIncrement(), "Orange Cake",
                                "Bizcocho esponjoso con sabor a naranja.", "3.50€",
                                "/images/MenuImages/Desserts/OrangeCake.png", "Desserts"));
                products.add(new Product(nextId.getAndIncrement(), "Red Velvet Cupcake",
                                "Clásico muffin Red Velvet con frosting de queso.", "3.00€",
                                "/images/MenuImages/Desserts/RedVelvetCupcake.png", "Desserts"));
                products.add(new Product(nextId.getAndIncrement(), "Strawberry Cake", "Pastel de fresas con nata.",
                                "3.80€", "/images/MenuImages/Desserts/StrawberryCake.png", "Desserts"));
                products.add(new Product(nextId.getAndIncrement(), "Vanilla Cupcake",
                                "Muffin de vainilla con frosting.", "2.80€",
                                "/images/MenuImages/Desserts/VanillaCupcake.png", "Desserts"));

                // --- Non-Coffee ---
                products.add(new Product(nextId.getAndIncrement(), "Herbal Tea", "Infusión relajante sin cafeína.",
                                "3.00€", "/images/MenuImages/Non-Coffee/HerbalTea.png", "Non-Coffee"));
                products.add(new Product(nextId.getAndIncrement(), "Chai Tea Latte",
                                "Té negro especiado con leche vaporizada.", "3.80€",
                                "/images/MenuImages/Non-Coffee/ChatTeaLatte.png", "Non-Coffee"));
                products.add(new Product(nextId.getAndIncrement(), "Golden Milk", "Leche con cúrcuma y especias.",
                                "4.00€", "/images/MenuImages/Non-Coffee/GoldenMilk.png", "Non-Coffee"));
                products.add(new Product(nextId.getAndIncrement(), "Hot Chocolate",
                                "Chocolate caliente espeso y cremoso.", "3.50€",
                                "/images/MenuImages/Non-Coffee/HotChocolate.png", "Non-Coffee"));
                products.add(new Product(nextId.getAndIncrement(), "Matcha Latte",
                                "Té verde matcha con leche vaporizada.", "4.20€",
                                "/images/MenuImages/Non-Coffee/MatchaLatte.png", "Non-Coffee"));
        }

        public List<Product> getAllProducts() {
                return products;
        }

        public void addProduct(Product product) {
                product.setId(nextId.getAndIncrement());
                product.setTimestamp(java.time.LocalDateTime.now());
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
                                newProduct.setId(id); // Ensure ID is preserved
                                newProduct.setTimestamp(products.get(i).getTimestamp()); // Preserve timestamp or
                                                                                         // update? Usually persist
                                                                                         // original creation time.
                                products.set(i, newProduct);
                                return;
                        }
                }
        }

        public void deleteProduct(Long id) {
                products.removeIf(p -> p.getId().equals(id));
        }
}
