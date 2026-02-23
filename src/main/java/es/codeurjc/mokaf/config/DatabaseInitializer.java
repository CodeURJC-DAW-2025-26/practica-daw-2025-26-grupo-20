package es.codeurjc.mokaf.config;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.util.List;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.codeurjc.mokaf.model.Category;
import es.codeurjc.mokaf.model.Image;
import es.codeurjc.mokaf.model.Product;
import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.repository.ImageRepository;
import es.codeurjc.mokaf.repository.ProductRepository;
import es.codeurjc.mokaf.repository.UserRepository;
import es.codeurjc.mokaf.model.Review;
import es.codeurjc.mokaf.repository.ReviewRepository;


@Component
public class DatabaseInitializer implements ApplicationRunner {

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public DatabaseInitializer(ProductRepository productRepository,
                           ImageRepository imageRepository,
                           UserRepository userRepository,
                           ReviewRepository reviewRepository) {
    this.productRepository = productRepository;
    this.imageRepository = imageRepository;
    this.userRepository = userRepository;
    this.reviewRepository = reviewRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {

        // 1) BORRAR TODO (si quieres mantener datos, quita esto)
        reviewRepository.deleteAll();
        productRepository.deleteAll();
        imageRepository.deleteAll();
        userRepository.deleteAll();

        // 2) CREAR USUARIOS DE EJEMPLO
        createUsers();

        // 3) Cargar catálogo de productos
        List<ProductSeed> seeds = List.of(
                // HOT
                new ProductSeed("Expreso", "Café negro fuerte y aromático.", "2.50", Category.HOT,
                        "/static/images/MenuImages/Hot/Expreso.png"),
                new ProductSeed("Capuccino", "Expreso con leche vaporizada y espuma.", "3.50", Category.HOT,
                        "/static/images/MenuImages/Hot/Capuccino.png"),
                new ProductSeed("Americano", "Expreso diluido con agua caliente.", "2.80", Category.HOT,
                        "/static/images/MenuImages/Hot/Americano.png"),
                new ProductSeed("Latte", "Expreso con una generosa cantidad de leche vaporizada.", "3.20", Category.HOT,
                        "/static/images/MenuImages/Hot/Latte.png"),

                // COLD
                new ProductSeed("Iced Latte", "Expreso y leche fría sobre hielo.", "4.00", Category.COLD,
                        "/static/images/MenuImages/Cold/IcedLatte.png"),
                new ProductSeed("Frappe", "Café batido con hielo, refrescante y cremoso.", "4.20", Category.COLD,
                        "/static/images/MenuImages/Cold/Frappe.png"),
                new ProductSeed("Iced Americano", "Expreso y agua fría servido sobre hielo.", "3.00", Category.COLD,
                        "/static/images/MenuImages/Cold/IcedAmericano.png"),
                new ProductSeed("Iced Vietnamese Coffe", "Café con leche condensada y hielo.", "4.50", Category.COLD,
                        "/static/images/MenuImages/Cold/IcedVietnameseCoffe.png"),

                // BLENDED
                new ProductSeed("Frapuccino", "Bebida de café mezclada con hielo y sabores.", "4.50", Category.BLENDED,
                        "/static/images/MenuImages/Blended/Frapuccino.png"),
                new ProductSeed("Chocolate Coffee Blend", "Mezcla de café y chocolate, batido con hielo.", "4.80", Category.BLENDED,
                        "/static/images/MenuImages/Blended/ChocolateCoffeeBlend.png"),
                new ProductSeed("Hazelnut Coffee Shake", "Batido de café con sirope de avellana.", "4.80", Category.BLENDED,
                        "/static/images/MenuImages/Blended/HazelnutCoffeeShake.png"),
                new ProductSeed("Vanilla Frappe", "Frappé suave con un toque de vainilla.", "4.60", Category.BLENDED,
                        "/static/images/MenuImages/Blended/VanillaFrappe.png"),

                // DESSERTS
                new ProductSeed("Croissants", "Clásico hojaldre francés.", "2.00", Category.DESSERTS,
                        "/static/images/MenuImages/Desserts/Croisants.png"),
                new ProductSeed("Chocolate Carrot Cake", "Pastel de zanahoria con cobertura de chocolate.", "3.50", Category.DESSERTS,
                        "/static/images/MenuImages/Desserts/ChocolateCarrotCake.png"),
                new ProductSeed("Chocolate Cupcake", "Muffin de chocolate con frosting.", "2.80", Category.DESSERTS,
                        "/static/images/MenuImages/Desserts/ChocolateCupcake.png"),
                new ProductSeed("Chocolate Green Tea Cupcake", "Muffin de té verde con corazón de chocolate.", "3.00", Category.DESSERTS,
                        "/static/images/MenuImages/Desserts/ChocolateGreenTeaCupcake.png"),
                new ProductSeed("Dulce De Leche Desserts", "Postre cremoso de dulce de leche.", "3.20", Category.DESSERTS,
                        "/static/images/MenuImages/Desserts/DulceDeLecheDesserts.png"),
                new ProductSeed("Orange Cake", "Bizcocho esponjoso con sabor a naranja.", "3.50", Category.DESSERTS,
                        "/static/images/MenuImages/Desserts/OrangeCake.png"),
                new ProductSeed("Red Velvet Cupcake", "Clásico muffin Red Velvet con frosting de queso.", "3.00", Category.DESSERTS,
                        "/static/images/MenuImages/Desserts/RedVelvetCupcake.png"),
                new ProductSeed("Strawberry Cake", "Pastel de fresas con nata.", "3.80", Category.DESSERTS,
                        "/static/images/MenuImages/Desserts/StrawberryCake.png"),
                new ProductSeed("Vanilla Cupcake", "Muffin de vainilla con frosting.", "2.80", Category.DESSERTS,
                        "/static/images/MenuImages/Desserts/VanillaCupcake.png"),

                // NON-COFFEE
                new ProductSeed("Herbal Tea", "Infusión relajante sin cafeína.", "3.00", Category.NON_COFFEE,
                        "/static/images/MenuImages/Non-Coffee/HerbalTea.png"),
                new ProductSeed("Chai Tea Latte", "Té negro especiado con leche vaporizada.", "3.80", Category.NON_COFFEE,
                        "/static/images/MenuImages/Non-Coffee/ChatTeaLatte.png"),
                new ProductSeed("Golden Milk", "Leche con cúrcuma y especias.", "4.00", Category.NON_COFFEE,
                        "/static/images/MenuImages/Non-Coffee/GoldenMilk.png"),
                new ProductSeed("Hot Chocolate", "Chocolate caliente espeso y cremoso.", "3.50", Category.NON_COFFEE,
                        "/static/images/MenuImages/Non-Coffee/HotChocolate.png"),
                new ProductSeed("Matcha Latte", "Té verde matcha con leche vaporizada.", "4.20", Category.NON_COFFEE,
                        "/static/images/MenuImages/Non-Coffee/MatchaLatte.png")
        );

        // 4) Insertar productos
        for (ProductSeed s : seeds) {
            Product p = new Product();
            p.setName(s.name);
            p.setDescription(s.description);
            p.setPriceBase(new BigDecimal(s.price));
            p.setCategory(s.category);

            Image img = new Image();
            img.setImageFile(loadAsBlob(s.classpathImagePath));
            p.setImage(img);

            productRepository.save(p);
        }

        createReviews();
        System.out.println(">>> DB seeded: " + seeds.size() + " products");
    }

    private void createUsers() {
        // ========== 2 ADMINS ==========
        
        // Admin 1
        User admin1 = new User();
        admin1.setName("Administrador Principal");
        admin1.setEmail("admin@mokaf.com");
        admin1.setPasswordHash(passwordEncoder.encode("admin123"));
        admin1.setRole(User.Role.ADMIN);
        admin1.setEmployeeId("EMP-001");
        userRepository.save(admin1);
        
        // Admin 2
        User admin2 = new User();
        admin2.setName("María González");
        admin2.setEmail("maria.admin@mokaf.com");
        admin2.setPasswordHash(passwordEncoder.encode("maria456"));
        admin2.setRole(User.Role.ADMIN);
        admin2.setEmployeeId("EMP-002");
        userRepository.save(admin2);
        
        // ========== 4 USUARIOS (CUSTOMERS) ==========
        
        // Usuario 1
        User user1 = new User();
        user1.setName("Carlos Rodríguez");
        user1.setEmail("carlos@email.com");
        user1.setPasswordHash(passwordEncoder.encode("carlos123"));
        user1.setRole(User.Role.CUSTOMER);
        userRepository.save(user1);
        
        // Usuario 2
        User user2 = new User();
        user2.setName("Ana Martínez");
        user2.setEmail("ana@email.com");
        user2.setPasswordHash(passwordEncoder.encode("ana456"));
        user2.setRole(User.Role.CUSTOMER);
        userRepository.save(user2);
        
        // Usuario 3
        User user3 = new User();
        user3.setName("Luis Fernández");
        user3.setEmail("luis@email.com");
        user3.setPasswordHash(passwordEncoder.encode("luis789"));
        user3.setRole(User.Role.CUSTOMER);
        userRepository.save(user3);
        
        // Usuario 4
        User user4 = new User();
        user4.setName("Sofía López");
        user4.setEmail("sofia@email.com");
        user4.setPasswordHash(passwordEncoder.encode("sofia012"));
        user4.setRole(User.Role.CUSTOMER);
        userRepository.save(user4);
        
        System.out.println(">>> Users created: 2 ADMINS + 4 CUSTOMERS");
    }

    private Blob loadAsBlob(String classpathPath) throws Exception {
        byte[] bytes;
        try (InputStream is = getClass().getResourceAsStream(classpathPath)) {
            if (is == null) {
                throw new IllegalStateException("No se encontró recurso: " + classpathPath);
            }
            bytes = is.readAllBytes();
        }
        return BlobProxy.generateProxy(bytes);
    }

    private static class ProductSeed {
        final String name;
        final String description;
        final String price;
        final Category category;
        final String classpathImagePath;

        ProductSeed(String name, String description, String price, Category category, String classpathImagePath) {
            this.name = name;
            this.description = description;
            this.price = price;
            this.category = category;
            this.classpathImagePath = classpathImagePath;
        }
    }

    private void createReviews() {

        List<User> users = userRepository.findAll();
        List<Product> products = productRepository.findAll();

        if (users.isEmpty() || products.isEmpty()) return;

        int[] stars = {5, 4, 5, 4, 5, 3};
        String[] texts = {
                "Café excelente, aromático y bien equilibrado.",
                "Muy buen servicio y presentación impecable.",
                "El capuccino estaba espectacular, repetiré.",
                "Buena relación calidad-precio. Recomendable.",
                "Postres muy ricos, especialmente la red velvet.",
                "Correcto, aunque lo prefiero un poco más intenso."
        };

        for (int i = 0; i < users.size(); i++) {
                User u = users.get(i);

                // Si NO quieres admins reseñando, usa esto:
                // if (u.getRole() == User.Role.ADMIN) continue;

                Product p = products.get(i % products.size());

                Review r = new Review();
                r.setUser(u);
                r.setProduct(p);
                r.setStars(stars[i % stars.length]);
                r.setText(texts[i % texts.length]);

                reviewRepository.save(r);
        }

        System.out.println(">>> Reviews created: " + users.size());
        }
}