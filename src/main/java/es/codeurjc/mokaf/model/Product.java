package es.codeurjc.mokaf.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Lob
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;

    @Column(name = "price_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceBase;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
        name = "product_allergens",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "allergen_id")
    )
    private Set<Allergen> allergens = new HashSet<>();

    public Product() {}

    public Product(String name, String description, Image image, BigDecimal priceBase, Category category) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.priceBase = priceBase;
        this.category = category;
    }

    // getters/setters (añade los que te falten)
    public Long getId() {
        return id; 
    }
    public String getName() {
        return name; 
    }
    public void setName(String name) { 
        this.name = name; 
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description; 
    }

    public Image getImage() { 
        return image; 
    }
    public void setImage(Image image) { 
        this.image = image; 
    }

    public BigDecimal getPriceBase() { 
        return priceBase; 
    }
    public void setPriceBase(BigDecimal priceBase) { 
        this.priceBase = priceBase; 
    }

    public Category getCategory() { 
        return category; 
    }
    public void setCategory(Category category) { 
        this.category = category; 
    }

    public boolean isActive() { 
        return active; 
    }
    public void setActive(boolean active) { 
        this.active = active; 
    }

    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }

    public Set<Allergen> getAllergens() { 
        return allergens; 
    }
    public void setAllergens(Set<Allergen> allergens) { 
        this.allergens = allergens; 
    }
}
