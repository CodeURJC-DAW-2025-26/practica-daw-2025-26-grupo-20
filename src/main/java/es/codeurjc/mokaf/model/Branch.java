package es.codeurjc.mokaf.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "branches")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "purchase_discount_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal purchaseDiscountPercent = BigDecimal.ZERO;

    @Lob
    private String description;


    public Branch() {}

    public Branch(String name, String description, BigDecimal purchaseDiscountPercent) {
        this.name = name;
        this.description = description;
        this.purchaseDiscountPercent = purchaseDiscountPercent;
    }

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

    public BigDecimal getPurchaseDiscountPercent() { 
        return purchaseDiscountPercent; 
    }
    public void setPurchaseDiscountPercent(BigDecimal purchaseDiscountPercent) { 
        this.purchaseDiscountPercent = purchaseDiscountPercent; 
    }

}
