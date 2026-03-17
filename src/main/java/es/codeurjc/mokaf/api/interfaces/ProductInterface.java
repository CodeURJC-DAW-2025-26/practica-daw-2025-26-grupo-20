package es.codeurjc.mokaf.api.interfaces;

import java.math.BigDecimal;
import java.util.List;

public interface ProductInterface {
    Long getId();
    void setId(Long id);
    String getName();
    void setName(String name);
    String getDescription();
    void setDescription(String description);
    BigDecimal getPriceBase();
    void setPriceBase(BigDecimal priceBase);
    String getCategory();
    void setCategory(String category);
    Long getImageId();
    void setImageId(Long imageId);
    java.time.LocalDateTime getCreatedAt();
    void setCreatedAt(java.time.LocalDateTime createdAt);
    List<AllergenInterface> getAllergens();
    void setAllergens(List<AllergenInterface> allergens);
}
