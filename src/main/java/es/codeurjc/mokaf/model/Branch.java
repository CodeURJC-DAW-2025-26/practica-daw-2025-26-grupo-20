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

    @Lob
    private String description;


    public Branch() {}

    public Branch(String name, String description, BigDecimal purchaseDiscountPercent) {
        this.name = name;
        this.description = description;
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

}
