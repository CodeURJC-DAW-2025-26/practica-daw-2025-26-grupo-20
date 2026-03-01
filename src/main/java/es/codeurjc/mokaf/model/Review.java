package es.codeurjc.mokaf.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int stars; // validar 1..5 en backend

    @Lob
    @Column(nullable = false)
    private String text;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Transient
    public String getCreatedAtFormatted() {
        if (createdAt == null) return "";
        java.time.format.DateTimeFormatter fmt =
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return createdAt.format(fmt);
}

    public Review() {}

    public Review(User user, Product product, int stars, String text) {
        this.user = user;
        this.product = product;
        this.stars = stars;
        this.text = text;
    }

    public Long getId() { 
        return id; 
    }

    public User getUser() { 
        return user; 
    }
    public void setUser(User user) { 
        this.user = user; 
    }

    public Product getProduct() { 
        return product; 
    }
    public void setProduct(Product product) { 
        this.product = product; 
    }

    public int getStars() { 
        return stars; 
    }
    public void setStars(int stars) { 
        this.stars = stars; 
    }

    public String getText() { 
        return text; 
    }
    public void setText(String text) { 
        this.text = text; 
    }

    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
}
