package es.codeurjc.mokaf.api.dto;

import es.codeurjc.mokaf.model.Allergen;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AllergenDTO(
    Long id,
    @NotBlank(message = "Allergen name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    String name
) {
}
