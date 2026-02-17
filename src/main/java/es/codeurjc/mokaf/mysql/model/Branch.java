package es.codeurjc.mokaf.mysql.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "branches")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    @Column(name = "purchase_discount_percent")
    private BigDecimal purchaseDiscountPercent;

    public Branch() {
    }

    public Branch(String name, String description, BigDecimal purchaseDiscountPercent) {
        this.name = name;
        this.description = description;
        this.purchaseDiscountPercent = purchaseDiscountPercent;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getPurchaseDiscountPercent() {
        return purchaseDiscountPercent;
    }

    public void setPurchaseDiscountPercent(BigDecimal purchaseDiscountPercent) {
        this.purchaseDiscountPercent = purchaseDiscountPercent;
    }
}
