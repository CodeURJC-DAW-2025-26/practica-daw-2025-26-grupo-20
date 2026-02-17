package es.codeurjc.mokaf.mysql.service;

import es.codeurjc.mokaf.mysql.model.Product;
import es.codeurjc.mokaf.mysql.model.Product.Category;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("mysqlProductService")
public class ProductService {

    private final List<Product> products = new ArrayList<>();

    public ProductService() {
        products.add(new Product((long) 1,"Expreso", "Café negro fuerte", new BigDecimal("2.50"), Category.HOT, true));
        products.add(new Product((long)2,"Iced Latte", "Café con leche y hielo", new BigDecimal("3.50"), Category.COLD, true));
        products.add(new Product((long)3,"Croissant", "Bolleria francesa", new BigDecimal("2.00"), Category.DESSERTS, true));
    }

    public List<Product> findAll() {
        return products;
    }

    public Optional<Product> findById(Long id) {
        return products.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId((long) (products.size() + 1));
        }
        products.add(product);
        return product;
    }
}
