package es.codeurjc.mokaf.api.dto;

import java.math.BigDecimal;
import java.util.Set;



public record ProductDetailDTO(
        Long id,
        String name,
        String description,
        String imageUrl,
        BigDecimal priceBase,
        String category,
        Set<AllergenDTO> allergens) {
}