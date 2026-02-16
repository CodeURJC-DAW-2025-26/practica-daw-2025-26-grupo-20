package es.codeurjc.mokaf.model;

import java.time.LocalDateTime;

public class Product {

    private Long id;
    private String name;
    private String description;
    private String basePrice;
    private String image;
    private String category;
    private LocalDateTime timestamp;

    // Used by JPA
    public Product() {

    }

    // Constructor
    public Product(Long id, String name, String description, String basePrice, String image, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.image = image;
        this.category = category;
        this.timestamp = LocalDateTime.now();
    }

    // Constructor without ID (for new products before saving)
    public Product(String name, String description, String basePrice, String image, String category) {
        this(null, name, description, basePrice, image, category);
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getBasePrice() {
        return basePrice;
    }

    public String getImage() {
        return image;
    }

    public String getCategory() {
        return category;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBasePrice(String basePrice) {
        this.basePrice = basePrice;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", basePrice='" + basePrice + '\'' +
                ", category='" + category + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}