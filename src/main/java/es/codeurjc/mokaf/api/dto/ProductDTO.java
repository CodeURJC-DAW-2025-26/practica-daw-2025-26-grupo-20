package es.codeurjc.mokaf.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductDTO(
        Long id,
        String name,
        String description,
        BigDecimal priceBase,
        String category,
        Long imageId,
        LocalDateTime createdAt,
        List<AllergenDTO> allergens
) {
}