package es.codeurjc.mokaf.api.dto;

import es.codeurjc.mokaf.model.Product;
import es.codeurjc.mokaf.api.interfaces.AllergenInterface;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ProductDTO implements es.codeurjc.mokaf.api.interfaces.ProductInterface {

    private Long id;
    private String name;
    private String description;
    private BigDecimal priceBase;
    private String category;
    private Long imageId;
    private LocalDateTime createdAt;
    private List<AllergenInterface> allergens;

    public ProductDTO() {
    }

    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.priceBase = product.getPriceBase();
        this.category = product.getCategory() != null ? product.getCategory().name() : null;
        this.imageId = product.getImage() != null ? product.getImage().getId() : null;
        this.createdAt = product.getCreatedAt();
        if (product.getAllergens() != null) {
            this.allergens = product.getAllergens().stream()
                    .map(es.codeurjc.mokaf.api.dto.AllergenDTO::new)
                    .collect(Collectors.toList());
        }
    }

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

    public BigDecimal getPriceBase() {
        return priceBase;
    }

    public void setPriceBase(BigDecimal priceBase) {
        this.priceBase = priceBase;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<AllergenInterface> getAllergens() {
        return allergens;
    }

    public void setAllergens(List<AllergenInterface> allergens) {
        this.allergens = allergens;
    }
}
