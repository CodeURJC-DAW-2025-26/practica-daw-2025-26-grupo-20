package es.codeurjc.mokaf.config;

import es.codeurjc.mokaf.model.*;
import es.codeurjc.mokaf.model.Order.Status;
import es.codeurjc.mokaf.model.User.Role;
import es.codeurjc.mokaf.repository.*;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.rowset.serial.SerialBlob;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.time.LocalDateTime;

@Configuration
@Profile("dev")
public class DatabaseInitializer {

    private Blob createBlob(String tag) throws Exception {
        byte[] bytes = ("DUMMY_IMAGE_" + tag).getBytes(StandardCharsets.UTF_8);
        return new SerialBlob(bytes);
    }

    @Bean
    public ApplicationRunner seedDatabase(
            BranchRepository branchRepo,
            UserRepository userRepo,
            ProductRepository productRepo,
            AllergenRepository allergenRepo,
            OrderRepository orderRepo,
            ReviewRepository reviewRepo
    ) {
        return args -> {

            // Evita resembrar si ya hay algo
            if (userRepo.count() > 0 || productRepo.count() > 0 || branchRepo.count() > 0) {
                return;
            }

            // -------- Branches --------
            Branch b1 = new Branch("Sucursal Centro", "Sucursal principal", new BigDecimal("10.00"));
            Branch b2 = new Branch("Sucursal Norte", "Sucursal secundaria", new BigDecimal("0.00"));
            branchRepo.save(b1);
            branchRepo.save(b2);

            // -------- Users (IMPORTANTE: NO guardes Image aparte) --------
            User u1 = new User("Cliente Demo", "cliente@mokaf.test", "hash-demo", Role.CUSTOMER);
            u1.setImage(new Image(createBlob("USER1")));

            User admin = new User("Admin Demo", "admin@mokaf.test", "hash-admin", Role.ADMIN);
            admin.setEmployeeId("EMP-0001");
            admin.setImage(new Image(createBlob("USER2")));

            userRepo.save(u1);
            userRepo.save(admin);

            // -------- Allergens --------
            Allergen gluten = new Allergen("GLUTEN");
            Allergen lactosa = new Allergen("LACTOSA");
            Allergen frutosSecos = new Allergen("FRUTOS_SECOS");
            allergenRepo.save(gluten);
            allergenRepo.save(lactosa);
            allergenRepo.save(frutosSecos);

            // -------- Products (IMPORTANTE: NO guardes Image aparte) --------
                Product p1 = new Product(
                    "Expreso",
                    "Café fuerte",
                    new Image(createBlob("PROD1")),
                    new BigDecimal("2.50"),
                    Category.HOT
                );

                Product p2 = new Product(
                    "Capuccino",
                    "Con leche y espuma",
                    new Image(createBlob("PROD2")),
                    new BigDecimal("3.50"),
                    Category.HOT
                );

                Product p3 = new Product(
                    "Brownie",
                    "Chocolate intenso",
                    new Image(createBlob("PROD3")),
                    new BigDecimal("2.90"),
                    Category.DESSERTS
                );

            // ManyToMany (si no quieres helpers, esto vale para seed)
            p2.getAllergens().add(lactosa);
            p3.getAllergens().add(gluten);
            p3.getAllergens().add(frutosSecos);

            productRepo.save(p1);
            productRepo.save(p2);
            productRepo.save(p3);

            // -------- Order (carrito) + items --------
            Order cart = new Order();
            cart.setUser(u1);
            cart.setBranch(b1);
            cart.setStatus(Status.CART);

            BigDecimal discountPercent = b1.getPurchaseDiscountPercent(); // 10.00
            cart.setDiscountPercent(discountPercent);

            int q1 = 2;
            BigDecimal unit1 = p1.getPriceBase();
            BigDecimal finalUnit1 = unit1.multiply(BigDecimal.ONE.subtract(discountPercent.divide(new BigDecimal("100"))));
            BigDecimal line1 = finalUnit1.multiply(new BigDecimal(q1));
            cart.addItem(new OrderItem(p1, q1, unit1, finalUnit1, line1));

            int q2 = 1;
            BigDecimal unit2 = p2.getPriceBase();
            BigDecimal finalUnit2 = unit2.multiply(BigDecimal.ONE.subtract(discountPercent.divide(new BigDecimal("100"))));
            BigDecimal line2 = finalUnit2.multiply(new BigDecimal(q2));
            cart.addItem(new OrderItem(p2, q2, unit2, finalUnit2, line2));

            BigDecimal subtotal = unit1.multiply(new BigDecimal(q1)).add(unit2.multiply(new BigDecimal(q2)));
            BigDecimal discountAmount = subtotal.multiply(discountPercent.divide(new BigDecimal("100")));
            BigDecimal total = subtotal.subtract(discountAmount);

            cart.setSubtotalAmount(subtotal);
            cart.setDiscountAmount(discountAmount);
            cart.setTotalAmount(total);

            orderRepo.save(cart);

            // -------- Review --------
            Review r1 = new Review(u1, p1, 5, "Muy bueno. Volveré a pedirlo.");
            reviewRepo.save(r1);

            // -------- Paid order demo (opcional) --------
            Order paid = new Order();
            paid.setUser(u1);
            paid.setBranch(b2);
            paid.setStatus(Status.PAID);
            paid.setPaidAt(LocalDateTime.now());
            paid.setDiscountPercent(b2.getPurchaseDiscountPercent()); // 0.00

            int q3 = 1;
            BigDecimal unit3 = p3.getPriceBase();
            BigDecimal finalUnit3 = unit3;
            BigDecimal line3 = finalUnit3.multiply(new BigDecimal(q3));
            paid.addItem(new OrderItem(p3, q3, unit3, finalUnit3, line3));

            paid.setSubtotalAmount(unit3);
            paid.setDiscountAmount(BigDecimal.ZERO);
            paid.setTotalAmount(unit3);

            orderRepo.save(paid);
        };
    }
}
