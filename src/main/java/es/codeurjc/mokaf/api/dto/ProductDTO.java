package es.codeurjc.mokaf.api.dto;

import es.codeurjc.mokaf.model.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record ProductDTO(
        Long id,
        @NotBlank(message = "Product name is required") @Size(max = 120) String name,
        @NotBlank(message = "Description is required") String description,
        @NotNull(message = "Price is required") @Min(value = 0, message = "Price cannot be negative") BigDecimal priceBase,
        @NotBlank(message = "Category is required") String category,
        Long imageId,
        LocalDateTime createdAt,
        List<AllergenDTO> allergens) {
}