package es.codeurjc.mokaf.config;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.codeurjc.mokaf.model.Allergen;
import es.codeurjc.mokaf.model.Branch;
import es.codeurjc.mokaf.model.Category;
import es.codeurjc.mokaf.model.Image;
import es.codeurjc.mokaf.model.Employee;
import es.codeurjc.mokaf.model.Order;
import es.codeurjc.mokaf.model.OrderItem;
import es.codeurjc.mokaf.model.Product;
import es.codeurjc.mokaf.model.Review;
import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.model.Faq;
import es.codeurjc.mokaf.repository.AllergenRepository;
import es.codeurjc.mokaf.repository.BranchRepository;
import es.codeurjc.mokaf.repository.ImageRepository;
import es.codeurjc.mokaf.repository.EmployeeRepository;
import es.codeurjc.mokaf.repository.OrderRepository;
import es.codeurjc.mokaf.repository.ProductRepository;
import es.codeurjc.mokaf.repository.ReviewRepository;
import es.codeurjc.mokaf.repository.UserRepository;
import es.codeurjc.mokaf.repository.FaqRepository;

@Component
public class DatabaseInitializer implements ApplicationRunner {

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final AllergenRepository allergenRepository;
    private final BranchRepository branchRepository;
    private final EmployeeRepository employeeRepository;
    private final OrderRepository orderRepository;
    private final FaqRepository faqRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(ProductRepository productRepository,
            ImageRepository imageRepository,
            UserRepository userRepository,
            ReviewRepository reviewRepository,
            AllergenRepository allergenRepository,
            BranchRepository branchRepository,
            EmployeeRepository employeeRepository,
            OrderRepository orderRepository,
            FaqRepository faqRepository,
            PasswordEncoder passwordEncoder) {
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.allergenRepository = allergenRepository;
        this.branchRepository = branchRepository;
        this.employeeRepository = employeeRepository;
        this.orderRepository = orderRepository;
        this.faqRepository = faqRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {

        // 1) BORRADO (hijos -> padres) para evitar violaciones de FK
        // Si tienes cascades bien configurados, podrías simplificar, pero así es
        // robusto.
        orderRepository.deleteAll();
        reviewRepository.deleteAll();
        userRepository.deleteAll(); // Delete users BEFORE employees and products

        // Si Product tiene relación 1-1/1-n con Image con cascade, puedes no borrar
        // imageRepository.
        // Pero como lo haces explícito, lo dejamos.
        productRepository.deleteAll();
        imageRepository.deleteAll();

        allergenRepository.deleteAll();
        employeeRepository.deleteAll();
        branchRepository.deleteAll();
        faqRepository.deleteAll();

        // 2) SUCURSALES Y EMPLEADOS (Usuarios dependen de empleados por employee_id)
        createBranches();
        createEmployees();

        // 3) USUARIOS
        createUsers();

        // 3) PRODUCT + IMAGES
        seedProducts();

        // 4) REVIEWS
        createReviews();

        // 5) allergens + Branches + Products
        createAllergens();
        updateProductsWithAllergens();

        // 6) Orders and carts
        createOrders();

        // 7) FAQS
        createFaqs();

        System.out.println(">>> DB seeded OK");
    }

    private void createFaqs() {
        Faq faq1 = new Faq("¿Hacen envíos a domicilio?",
                "Sí, realizamos envíos a través de Glovo y Uber Eats en un radio de 5km de nuestras sucursales.");
        Faq faq2 = new Faq("¿Tienen opciones sin gluten?",
                "Contamos con una variedad de opciones sin gluten, aunque advertimos de la posible contaminación cruzada en nuestra cocina.");
        Faq faq3 = new Faq("¿Puedo reservar una mesa?",
                "Aceptamos reservas de mesa con una antelación mínima de 24 horas contactando a nuestra sucursal correspondiente o mediante nuestro formulario de contacto.");
        Faq faq4 = new Faq("¿Venden granos de café?",
                "Sí, vendemos nuestros propios blends de café de especialidad de orígenes seleccionados recién tostados.");

        faqRepository.saveAll(Arrays.asList(faq1, faq2, faq3, faq4));
        System.out.println(">>> FAQs created: 4 FAQs seeded");
    }

    private void seedProducts() throws Exception {
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
                new ProductSeed("Chocolate Coffee Blend", "Mezcla de café y chocolate, batido con hielo.", "4.80",
                        Category.BLENDED, "/static/images/MenuImages/Blended/ChocolateCoffeeBlend.png"),
                new ProductSeed("Hazelnut Coffee Shake", "Batido de café con sirope de avellana.", "4.80",
                        Category.BLENDED, "/static/images/MenuImages/Blended/HazelnutCoffeeShake.png"),
                new ProductSeed("Vanilla Frappe", "Frappé suave con un toque de vainilla.", "4.60", Category.BLENDED,
                        "/static/images/MenuImages/Blended/VanillaFrappe.png"),

                // DESSERTS
                new ProductSeed("Croissants", "Clásico hojaldre francés.", "2.00", Category.DESSERTS,
                        "/static/images/MenuImages/Desserts/Croisants.png"),
                new ProductSeed("Chocolate Carrot Cake", "Pastel de zanahoria con cobertura de chocolate.", "3.50",
                        Category.DESSERTS, "/static/images/MenuImages/Desserts/ChocolateCarrotCake.png"),
                new ProductSeed("Chocolate Cupcake", "Muffin de chocolate con frosting.", "2.80", Category.DESSERTS,
                        "/static/images/MenuImages/Desserts/ChocolateCupcake.png"),
                new ProductSeed("Chocolate Green Tea Cupcake", "Muffin de té verde con corazón de chocolate.", "3.00",
                        Category.DESSERTS, "/static/images/MenuImages/Desserts/ChocolateGreenTeaCupcake.png"),
                new ProductSeed("Dulce De Leche Desserts", "Postre cremoso de dulce de leche.", "3.20",
                        Category.DESSERTS,
                        "/static/images/MenuImages/Desserts/DulceDeLecheDesserts.png"),
                new ProductSeed("Orange Cake", "Bizcocho esponjoso con sabor a naranja.", "3.50", Category.DESSERTS,
                        "/static/images/MenuImages/Desserts/OrangeCake.png"),
                new ProductSeed("Red Velvet Cupcake", "Clásico muffin Red Velvet con frosting de queso.", "3.00",
                        Category.DESSERTS, "/static/images/MenuImages/Desserts/RedVelvetCupcake.png"),
                new ProductSeed("Strawberry Cake", "Pastel de fresas con nata.", "3.80", Category.DESSERTS,
                        "/static/images/MenuImages/Desserts/StrawberryCake.png"),
                new ProductSeed("Vanilla Cupcake", "Muffin de vainilla con frosting.", "2.80", Category.DESSERTS,
                        "/static/images/MenuImages/Desserts/VanillaCupcake.png"),

                // NON-COFFEE
                new ProductSeed("Herbal Tea", "Infusión relajante sin cafeína.", "3.00", Category.NON_COFFEE,
                        "/static/images/MenuImages/Non-Coffee/HerbalTea.png"),
                new ProductSeed("Chai Tea Latte", "Té negro especiado con leche vaporizada.", "3.80",
                        Category.NON_COFFEE,
                        "/static/images/MenuImages/Non-Coffee/ChatTeaLatte.png"),
                new ProductSeed("Golden Milk", "Leche con cúrcuma y especias.", "4.00", Category.NON_COFFEE,
                        "/static/images/MenuImages/Non-Coffee/GoldenMilk.png"),
                new ProductSeed("Hot Chocolate", "Chocolate caliente espeso y cremoso.", "3.50", Category.NON_COFFEE,
                        "/static/images/MenuImages/Non-Coffee/HotChocolate.png"),
                new ProductSeed("Matcha Latte", "Té verde matcha con leche vaporizada.", "4.20", Category.NON_COFFEE,
                        "/static/images/MenuImages/Non-Coffee/MatchaLatte.png"));

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

        System.out.println(">>> Products seeded: " + seeds.size());
    }

    private void createUsers() {
        User admin1 = new User();
        admin1.setName("Administrador Principal");
        admin1.setEmail("admin@mokaf.com");
        admin1.setPasswordHash(passwordEncoder.encode("admin123"));
        admin1.setRole(User.Role.ADMIN);
        admin1.setEmployeeId("EMP-001");
        userRepository.save(admin1);

        User admin2 = new User();
        admin2.setName("María González");
        admin2.setEmail("maria.admin@mokaf.com");
        admin2.setPasswordHash(passwordEncoder.encode("maria456"));
        admin2.setRole(User.Role.ADMIN);
        admin2.setEmployeeId("EMP-002");
        userRepository.save(admin2);

        User user1 = new User();
        user1.setName("Carlos Rodríguez");
        user1.setEmail("carlos@email.com");
        user1.setPasswordHash(passwordEncoder.encode("carlos123"));
        user1.setRole(User.Role.CUSTOMER);
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("Ana Martínez");
        user2.setEmail("ana@email.com");
        user2.setPasswordHash(passwordEncoder.encode("ana456"));
        user2.setRole(User.Role.CUSTOMER);
        userRepository.save(user2);

        User user3 = new User();
        user3.setName("Luis Fernández");
        user3.setEmail("luis@email.com");
        user3.setPasswordHash(passwordEncoder.encode("luis789"));
        user3.setRole(User.Role.CUSTOMER);
        userRepository.save(user3);

        User user4 = new User();
        user4.setName("Sofía López");
        user4.setEmail("sofia@email.com");
        user4.setPasswordHash(passwordEncoder.encode("sofia012"));
        user4.setRole(User.Role.CUSTOMER);
        userRepository.save(user4);

        // Additional employees as users
        User emp1 = new User("Elinee Freites", "elinee@mokaf.com", passwordEncoder.encode("elinee123"),
                User.Role.EMPLOYEE);
        emp1.setEmployeeId("EMP-003");
        userRepository.save(emp1);

        User emp2 = new User("Jordi Guix", "jordi@mokaf.com", passwordEncoder.encode("jordi123"), User.Role.EMPLOYEE);
        emp2.setEmployeeId("EMP-004");
        userRepository.save(emp2);

        User emp3 = new User("Alexandra Cararus", "alexandra@mokaf.com", passwordEncoder.encode("alex123"),
                User.Role.EMPLOYEE);
        emp3.setEmployeeId("EMP-005");
        userRepository.save(emp3);

        User emp4 = new User("Guillermo Velázquez", "guillermo@mokaf.com", passwordEncoder.encode("guille123"),
                User.Role.EMPLOYEE);
        emp4.setEmployeeId("EMP-006");
        userRepository.save(emp4);

        User emp5 = new User("Gonzalo Pérez", "gonzalo@mokaf.com", passwordEncoder.encode("gonza123"),
                User.Role.EMPLOYEE);
        emp5.setEmployeeId("EMP-007");
        userRepository.save(emp5);

        System.out.println(">>> Users created: 2 ADMINS + 4 CUSTOMERS + 5 EMPLOYEES");
    }

    private void createReviews() {
        List<User> users = userRepository.findAll();
        List<Product> products = productRepository.findAll();
        if (users.isEmpty() || products.isEmpty())
            return;

        // Filter only customers (no admins)
        List<User> customers = users.stream()
                .filter(u -> u.getRole() == User.Role.CUSTOMER)
                .toList();

        if (customers.isEmpty()) {
            System.out.println(">>> No customers found for reviews");
            return;
        }

        System.out.println(">>> Creating reviews for different products...");

        // Define reviews for specific products - CONTENT IN SPANISH FOR USERS
        // Format: [productName, stars, reviewText]
        List<Object[]> reviewData = Arrays.asList(
                // HOT COFFEES
                new Object[] { "Expreso", 5, "Café perfecto, fuerte y aromático. ¡Así me gusta!" },
                new Object[] { "Expreso", 4, "Buena intensidad, quizás un poco fuerte para mi gusto." },
                new Object[] { "Expreso", 5, "El mejor expreso de la zona, la crema es perfecta." },
                new Object[] { "Capuccino", 5, "Cremoso y delicioso, la espuma es perfecta." },
                new Object[] { "Capuccino", 4, "Muy buen capuccino, ¿quizás un poco más de chocolate?" },
                new Object[] { "Capuccino", 5, "Mi capuccino favorito, siempre consistente." },
                new Object[] { "Latte", 5, "Suave y cremoso, simplemente perfecto." },
                new Object[] { "Latte", 4, "Buen latte, temperatura ideal." },
                new Object[] { "Americano", 3, "Bueno pero un poco aguado para mi gusto." },

                // COLD COFFEES
                new Object[] { "Iced Latte", 5, "Refrescante y fuerte, ¡perfecto para el verano!" },
                new Object[] { "Iced Latte", 4, "Muy bueno, quizás un poco más de hielo." },
                new Object[] { "Frappe", 5, "El mejor frappe que he probado, ¡cremoso y delicioso!" },
                new Object[] { "Frappe", 5, "¡Muy refrescante, me encanta!" },
                new Object[] { "Iced Vietnamese Coffe", 5, "Sabor auténtico, dulce y fuerte." },
                new Object[] { "Iced Vietnamese Coffe", 4, "Bueno pero demasiado dulce para mí." },

                // DESSERTS
                new Object[] { "Croissants", 5, "Hojaldrado y mantecoso, ¡como en París!" },
                new Object[] { "Croissants", 4, "Muy buenos croissants, frescos cada día." },
                new Object[] { "Croissants", 5, "¡Los mejores croissants de la ciudad!" },
                new Object[] { "Chocolate Carrot Cake", 5, "Jugoso y chocolatoso, ¡increíble!" },
                new Object[] { "Chocolate Carrot Cake", 5, "Mi tarta favorita, siempre fresca." },
                new Object[] { "Red Velvet Cupcake", 5, "Red velvet perfecto, frosting cremoso." },
                new Object[] { "Red Velvet Cupcake", 4, "Bueno pero un poco seco." },
                new Object[] { "Vanilla Cupcake", 4, "Vainilla clásica, muy bueno." },
                new Object[] { "Strawberry Cake", 5, "Fresas frescas, ligero y delicioso." },
                new Object[] { "Chocolate Cupcake", 5, "Sabor a chocolate intenso, ¡espectacular!" },
                new Object[] { "Orange Cake", 4, "Agradable sabor cítrico, muy refrescante." },

                // NON-COFFEE
                new Object[] { "Chai Tea Latte", 5, "Especias perfectas, muy aromático." },
                new Object[] { "Chai Tea Latte", 4, "Buen chai, podría ser más especiado." },
                new Object[] { "Hot Chocolate", 5, "Rico y cremoso, perfecto para días fríos." },
                new Object[] { "Hot Chocolate", 5, "¡El mejor chocolate caliente!" },
                new Object[] { "Matcha Latte", 4, "Buen matcha, suave y cremoso." },

                // BLENDED
                new Object[] { "Frapuccino", 5, "Café mezclado perfecto, ¡no demasiado dulce!" },
                new Object[] { "Chocolate Coffee Blend", 5, "Combinación increíble de café y chocolate." },
                new Object[] { "Hazelnut Coffee Shake", 5, "Me encanta el sabor a avellana, equilibrio perfecto." });

        Random random = new Random(42); // Fixed seed for reproducibility

        // Create reviews distributing among customers
        for (Object[] data : reviewData) {
            String productName = (String) data[0];
            int stars = (int) data[1];
            String text = (String) data[2];

            // Find the product
            Product product = findProductByName(products, productName);
            if (product == null) {
                System.out.println(">>> Product not found: " + productName);
                continue;
            }

            // Select random customer
            User customer = customers.get(random.nextInt(customers.size()));

            // Create and save review
            Review review = new Review();
            review.setUser(customer);
            review.setProduct(product);
            review.setStars(stars);
            review.setText(text);

            reviewRepository.save(review);
        }

    }

    private void createAllergens() {
        List<String> allergenNames = Arrays.asList(
                "Gluten", "Lácteos", "Huevos", "Frutos secos", "Cacahuetes", "Soja", "Sulfitos", "Sésamo");

        for (String name : allergenNames) {
            allergenRepository.save(new Allergen(name));
        }

        System.out.println(">>> Allergens created: " + allergenNames.size());
    }

    private void createBranches() {
        Branch barcelona = new Branch();
        barcelona.setName("Barcelona - Paseo de Gracia");
        barcelona.setDescription(
                "Nuestra sucursal emblemática en el corazón de Barcelona. " +
                        "Un espacio moderno y acogedor donde podrás disfrutar de " +
                        "nuestros mejores cafés de especialidad mientras trabajas " +
                        "o te reúnes con amigos.\n\n" +
                        "Localización\n" +
                        "Paseo de Gracia 85, 08008 Barcelona\n\n" +
                        "Horario\n" +
                        "Lunes a Domingo: 10:00 - 20:00\n\n");
        barcelona.setPurchaseDiscountPercent(BigDecimal.valueOf(10));
        branchRepository.save(barcelona);

        Branch madrid = new Branch();
        madrid.setName("Madrid - Gran Vía");
        madrid.setDescription(
                "Ubicada en la Gran Vía madrileña, esta sucursal combina " +
                        "elegancia y tradición. Perfecta para una pausa en tu día " +
                        "de compras o para disfrutar de nuestros pasteles artesanales.\n\n" +
                        "Localización\n" +
                        "Gran Vía 42, 28013 Madrid\n\n" +
                        "Horario\n" +
                        "Lunes a Domingo: 10:00 - 20:00\n\n");
        madrid.setPurchaseDiscountPercent(BigDecimal.valueOf(15));
        branchRepository.save(madrid);

        Branch mostoles = new Branch();
        mostoles.setName("Móstoles - Centro");
        mostoles.setDescription(
                "Nuestra cafetería más familiar y acogedora. Con una amplia " +
                        "terraza y zona de juegos, es el lugar ideal para disfrutar " +
                        "en familia. Ambiente relajado y servicio excepcional.\n\n" +
                        "Localización\n" +
                        "Calle Dos de Mayo 15, 28935 Móstoles\n\n" +
                        "Horario\n" +
                        "Lunes a Domingo: 10:00 - 20:00\n\n");
        mostoles.setPurchaseDiscountPercent(BigDecimal.valueOf(25));
        branchRepository.save(mostoles);

        Branch santander = new Branch();
        santander.setName("Santander - Paseo de Pereda");
        santander.setDescription(
                "Con vistas al mar Cantábrico, esta sucursal ofrece una " +
                        "experiencia única. Disfruta de nuestro café mientras " +
                        "contemplas el océano. Ambiente tranquilo y perfecto para " +
                        "relajarse.\n\n" +
                        "Localización\n" +
                        "Paseo de Pereda 28, 39004 Santander\n\n" +
                        "Horario\n" +
                        "Lunes a Domingo: 10:00 - 20:00\n\n");
        santander.setPurchaseDiscountPercent(BigDecimal.valueOf(10));
        branchRepository.save(santander);

        System.out.println(">>> Branches created: 4 branches");
    }

    private void createEmployees() {
        List<Branch> branches = branchRepository.findAll();
        if (branches.isEmpty())
            return;

        Branch madrid = branches.stream().filter(b -> b.getName().contains("Madrid")).findFirst()
                .orElseGet(() -> branches.isEmpty() ? null : branches.get(0));
        Branch barcelona = branches.stream().filter(b -> b.getName().contains("Barcelona")).findFirst()
                .orElseGet(() -> branches.isEmpty() ? null : branches.get(0));

        // Administradores (Referenciados en createUsers)
        Employee admin1 = new Employee("EMP-001", "Administrador", "Principal", "Administrador del Sistema",
                "Direccion",
                new BigDecimal("4000.00"), madrid,
                "Gestor general de la plataforma Mokaf.", "admin@mokaf.com",
                "../images/Profile/default.png");

        Employee admin2 = new Employee("EMP-002", "María", "González", "Supervisora", "Direccion",
                new BigDecimal("3200.00"), madrid,
                "Supervisora de operaciones y atención al cliente.", "maria.admin@mokaf.com",
                "../images/Profile/default.png");

        // Equipo Público
        Employee elinee = new Employee("EMP-003", "Elinee", "Freites", "Fundadora & Master Roaster",
                "Atencion al cliente",
                new BigDecimal("3500.00"), madrid,
                "Visionaria detrás de cada blend exclusivo de Mokaf.", "elinee@mokaf.com",
                "../images/Profile/elinee.png");

        Employee jordi = new Employee("EMP-004", "Jordi", "Guix", "Barista Principal", "Atencion al cliente",
                new BigDecimal("2500.00"),
                barcelona,
                "El artista que convierte el cafe en lienzo.", "jordi@mokaf.com", "../images/Profile/jordi.png");

        Employee alexandra = new Employee("EMP-005", "Alexandra", "Cararus", "Gerente de Experiencia",
                "Atencion al cliente",
                new BigDecimal("2800.00"), madrid,
                "Asegurando que cada visita sea inolvidable.", "alexandra@mokaf.com",
                "../images/Profile/alexandra.png");

        Employee guillermo = new Employee("EMP-006", "Guillermo", "Velázquez", "Director de Tecnología",
                "Atencion al cliente",
                new BigDecimal("3200.00"), barcelona,
                "Innovando para llevar la experiencia Mokaf al mundo digital.", "guillermo@mokaf.com",
                "../images/Profile/guillermo.png");

        Employee gonzalo = new Employee("EMP-007", "Gonzalo", "Pérez", "Estratega de Negocio", "Atencion al cliente",
                new BigDecimal("3100.00"), madrid,
                "Expandiendo horizontes y buscando nuevas oportunidades.", "gonzalo@mokaf.com",
                "../images/Profile/Gonzalo.png");

        employeeRepository.saveAll(Arrays.asList(admin1, admin2, elinee, jordi, alexandra, guillermo, gonzalo));
        System.out.println(">>> Employees created: 7 real members seeded (including admins)");
    }

    private void updateProductsWithAllergens() {
        List<Product> products = productRepository.findAll();
        List<Allergen> allergens = allergenRepository.findAll();

        if (products.isEmpty() || allergens.isEmpty()) {
            System.out.println(">>> Cannot update products with allergens: missing products or allergens");
            return;
        }

        Allergen gluten = findAllergenByName(allergens, "Gluten");
        Allergen lacteos = findAllergenByName(allergens, "Lácteos");
        Allergen huevos = findAllergenByName(allergens, "Huevos");
        Allergen frutosSecos = findAllergenByName(allergens, "Frutos secos");
        Allergen soja = findAllergenByName(allergens, "Soja");

        for (Product product : products) {
            Set<Allergen> productAllergens = new HashSet<>();

            switch (product.getName()) {
                case "Capuccino":
                case "Latte":
                case "Iced Latte":
                case "Frappe":
                case "Iced Vietnamese Coffe":
                case "Frapuccino":
                case "Vanilla Frappe":
                case "Chai Tea Latte":
                case "Golden Milk":
                case "Hot Chocolate":
                    if (lacteos != null)
                        productAllergens.add(lacteos);
                    break;

                case "Chocolate Coffee Blend":
                    if (lacteos != null)
                        productAllergens.add(lacteos);
                    if (gluten != null)
                        productAllergens.add(gluten);
                    break;

                case "Hazelnut Coffee Shake":
                    if (lacteos != null)
                        productAllergens.add(lacteos);
                    if (frutosSecos != null)
                        productAllergens.add(frutosSecos);
                    break;

                case "Croissants":
                    if (gluten != null)
                        productAllergens.add(gluten);
                    if (lacteos != null)
                        productAllergens.add(lacteos);
                    if (huevos != null)
                        productAllergens.add(huevos);
                    break;

                case "Chocolate Carrot Cake":
                    if (gluten != null)
                        productAllergens.add(gluten);
                    if (huevos != null)
                        productAllergens.add(huevos);
                    if (frutosSecos != null)
                        productAllergens.add(frutosSecos);
                    break;

                case "Chocolate Cupcake":
                case "Red Velvet Cupcake":
                case "Strawberry Cake":
                case "Vanilla Cupcake":
                case "Orange Cake":
                    if (gluten != null)
                        productAllergens.add(gluten);
                    if (lacteos != null)
                        productAllergens.add(lacteos);
                    if (huevos != null)
                        productAllergens.add(huevos);
                    break;

                case "Chocolate Green Tea Cupcake":
                    if (gluten != null)
                        productAllergens.add(gluten);
                    if (lacteos != null)
                        productAllergens.add(lacteos);
                    if (huevos != null)
                        productAllergens.add(huevos);
                    if (soja != null)
                        productAllergens.add(soja);
                    break;

                case "Dulce De Leche Desserts":
                    if (lacteos != null)
                        productAllergens.add(lacteos);
                    if (huevos != null)
                        productAllergens.add(huevos);
                    break;

                case "Matcha Latte":
                    if (lacteos != null)
                        productAllergens.add(lacteos);
                    if (soja != null)
                        productAllergens.add(soja);
                    break;

                default:
                    break;
            }

            if (!productAllergens.isEmpty()) {
                product.setAllergens(productAllergens);
                productRepository.save(product);
            }
        }

        System.out.println(">>> Products updated with allergens");
    }

    private void createOrders() {
        List<User> customers = userRepository.findAll().stream()
                .filter(user -> user.getRole() == User.Role.CUSTOMER)
                .toList();

        List<Branch> branches = branchRepository.findAll();
        List<Product> products = productRepository.findAll();

        if (customers.isEmpty() || branches.isEmpty() || products.isEmpty()) {
            System.out.println(">>> Cannot create orders: missing customers, branches, or products");
            return;
        }

        // Categorize products by type for easy access
        List<Product> hotCoffees = products.stream()
                .filter(p -> p.getCategory() == Category.HOT)
                .toList();

        List<Product> coldCoffees = products.stream()
                .filter(p -> p.getCategory() == Category.COLD)
                .toList();

        List<Product> desserts = products.stream()
                .filter(p -> p.getCategory() == Category.DESSERTS)
                .toList();

        List<Product> nonCoffee = products.stream()
                .filter(p -> p.getCategory() == Category.NON_COFFEE)
                .toList();

        List<Product> blended = products.stream()
                .filter(p -> p.getCategory() == Category.BLENDED)
                .toList();

        System.out.println(">>> Creating orders with specific quantities...");
        int orderCounter = 0;

        // ============ HOT COFFEES: 13 units ============
        orderCounter = createCategoryOrders(customers, branches, hotCoffees, 13, orderCounter, "HOT COFFEES");

        // ============ COLD COFFEES: 24 units ============
        orderCounter = createCategoryOrders(customers, branches, coldCoffees, 24, orderCounter, "COLD COFFEES");

        // ============ DESSERTS: 14 units with variety ============
        orderCounter = createDessertOrders(customers, branches, desserts, orderCounter);

        // ============ NON-COFFEE: 10 units ============
        orderCounter = createCategoryOrders(customers, branches, nonCoffee, 10, orderCounter, "NON-COFFEE");

        // ============ BLENDED: 8 units ============
        orderCounter = createCategoryOrders(customers, branches, blended, 8, orderCounter, "BLENDED");

        // ============ Create one active cart ============
        createActiveCart(customers, branches, products);

        long orderCount = orderRepository.count();
        System.out.println(">>> Orders created: " + orderCount + " orders (including active carts)");
    }

    /**
     * Creates orders to achieve a specific total quantity of products from a
     * category
     * 
     * @return updated order counter
     */
    private int createCategoryOrders(List<User> customers, List<Branch> branches,
            List<Product> products, int targetQuantity,
            int startOrderIndex, String categoryName) {
        if (products.isEmpty()) {
            System.out.println(">>> No products available for category: " + categoryName);
            return startOrderIndex;
        }

        int remainingQuantity = targetQuantity;
        int orderIndex = startOrderIndex;
        Random random = new Random(42 + orderIndex); // Different seed for variety but still deterministic

        while (remainingQuantity > 0) {
            // Select customer and branch cyclically
            User customer = customers.get(orderIndex % customers.size());
            Branch branch = branches.get(orderIndex % branches.size());

            // Create order
            Order order = new Order();
            order.setUser(customer);
            order.setBranch(branch);
            order.setStatus(Order.Status.PAID);
            order.setPaidAt(LocalDateTime.now().minusDays(orderIndex * 2)); // Spread over time

            BigDecimal subtotal = BigDecimal.ZERO;
            int itemsInThisOrder = 0;

            // Add items to this order (max 3 items per order)
            while (remainingQuantity > 0 && itemsInThisOrder < 3) {
                // Select random product from the category
                Product product = products.get(random.nextInt(products.size()));

                // Determine quantity for this item (1-3 units, but not exceeding remaining)
                int quantity = Math.min(1 + random.nextInt(3), remainingQuantity);

                OrderItem item = new OrderItem();
                item.setProduct(product);
                item.setQuantity(quantity);
                item.setUnitPrice(product.getPriceBase());
                item.setFinalUnitPrice(product.getPriceBase());

                BigDecimal lineTotal = product.getPriceBase()
                        .multiply(BigDecimal.valueOf(quantity));
                item.setLineTotal(lineTotal);

                order.addItem(item);
                subtotal = subtotal.add(lineTotal);

                remainingQuantity -= quantity;
                itemsInThisOrder++;
            }

            if (itemsInThisOrder > 0) {
                order.setSubtotalAmount(subtotal);
                order.setDiscountPercent(BigDecimal.ZERO);
                order.setDiscountAmount(BigDecimal.ZERO);
                order.setTotalAmount(subtotal);

                orderRepository.save(order);
            }

            orderIndex++;
        }

        System.out.println(">>> Created orders for " + targetQuantity + " " + categoryName + " units");
        return orderIndex;
    }

    /**
     * Creates specific dessert orders with variety: cakes, croissants, cupcakes
     * 
     * @return updated order counter
     */
    private int createDessertOrders(List<User> customers, List<Branch> branches,
            List<Product> desserts, int startOrderIndex) {
        if (desserts.isEmpty()) {
            System.out.println(">>> No desserts available");
            return startOrderIndex;
        }

        // Map specific desserts by name (with fallbacks)
        Product croissants = findProductByName(desserts, "Croissants");
        Product chocolateCake = findProductByName(desserts, "Chocolate Carrot Cake");
        Product redVelvet = findProductByName(desserts, "Red Velvet Cupcake");
        Product vanillaCupcake = findProductByName(desserts, "Vanilla Cupcake");
        Product strawberryCake = findProductByName(desserts, "Strawberry Cake");
        Product chocolateCupcake = findProductByName(desserts, "Chocolate Cupcake");
        Product orangeCake = findProductByName(desserts, "Orange Cake");

        int orderIndex = startOrderIndex;

        // 4 Croissants (2 orders of 2 units each)
        orderIndex = createSingleItemOrder(customers, branches, croissants, 2, orderIndex++);
        orderIndex = createSingleItemOrder(customers, branches, croissants, 2, orderIndex++);

        // 2 Chocolate Cakes (1 order of 2 units)
        orderIndex = createSingleItemOrder(customers, branches, chocolateCake, 2, orderIndex++);

        // 2 Red Velvet Cupcakes
        orderIndex = createSingleItemOrder(customers, branches, redVelvet, 1, orderIndex++);
        orderIndex = createSingleItemOrder(customers, branches, redVelvet, 1, orderIndex++);

        // 2 Vanilla Cupcakes
        orderIndex = createSingleItemOrder(customers, branches, vanillaCupcake, 1, orderIndex++);
        orderIndex = createSingleItemOrder(customers, branches, vanillaCupcake, 1, orderIndex++);

        // 2 Strawberry Cakes
        orderIndex = createSingleItemOrder(customers, branches, strawberryCake, 1, orderIndex++);
        orderIndex = createSingleItemOrder(customers, branches, strawberryCake, 1, orderIndex++);

        // 2 Chocolate Cupcakes
        orderIndex = createSingleItemOrder(customers, branches, chocolateCupcake, 1, orderIndex++);
        orderIndex = createSingleItemOrder(customers, branches, chocolateCupcake, 1, orderIndex++);

        // 2 Orange Cakes
        orderIndex = createSingleItemOrder(customers, branches, orangeCake, 1, orderIndex++);
        orderIndex = createSingleItemOrder(customers, branches, orangeCake, 1, orderIndex++);

        System.out.println(
                ">>> Created specific dessert orders: 4 croissants, 2 chocolate cakes, 2 red velvet, 2 vanilla, 2 strawberry, 2 chocolate cupcakes, 2 orange cakes");
        return orderIndex;
    }

    /**
     * Helper method to create an order with a single item
     * 
     * @return updated order counter
     */
    private int createSingleItemOrder(List<User> customers, List<Branch> branches,
            Product product, int quantity, int orderIndex) {
        if (product == null)
            return orderIndex;

        User customer = customers.get(orderIndex % customers.size());
        Branch branch = branches.get(orderIndex % branches.size());

        Order order = new Order();
        order.setUser(customer);
        order.setBranch(branch);
        order.setStatus(Order.Status.PAID);
        order.setPaidAt(LocalDateTime.now().minusDays(orderIndex * 3));

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setUnitPrice(product.getPriceBase());
        item.setFinalUnitPrice(product.getPriceBase());

        BigDecimal lineTotal = product.getPriceBase()
                .multiply(BigDecimal.valueOf(quantity));
        item.setLineTotal(lineTotal);

        order.addItem(item);

        order.setSubtotalAmount(lineTotal);
        order.setDiscountPercent(BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setTotalAmount(lineTotal);

        orderRepository.save(order);

        return orderIndex + 1;
    }

    private void createActiveCart(List<User> customers, List<Branch> branches, List<Product> products) {
        if (customers.isEmpty() || branches.isEmpty() || products.isEmpty()) {
            return;
        }

        Order cart = new Order();
        cart.setUser(customers.get(0)); // First customer
        cart.setBranch(branches.get(2)); // Móstoles branch
        cart.setStatus(Order.Status.CART);

        BigDecimal subtotal = BigDecimal.ZERO;

        cart.setSubtotalAmount(subtotal);
        cart.setDiscountPercent(BigDecimal.ZERO);
        cart.setDiscountAmount(BigDecimal.ZERO);
        cart.setTotalAmount(subtotal);

        orderRepository.save(cart);
    }

    /**
     * Helper method to find a product by name in a list
     */
    private Product findProductByName(List<Product> products, String name) {
        return products.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private Allergen findAllergenByName(List<Allergen> allergens, String name) {
        return allergens.stream()
                .filter(a -> a.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private Blob loadAsBlob(String classpathPath) throws Exception {
        byte[] bytes;
        try (InputStream is = getClass().getResourceAsStream(classpathPath)) {
            if (is == null) {
                throw new IllegalStateException("No se encontró recurso: " + classpathPath);
            }
            bytes = is.readAllBytes();
        }
        return new javax.sql.rowset.serial.SerialBlob(bytes);
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
}