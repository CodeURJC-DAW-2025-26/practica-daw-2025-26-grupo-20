package es.codeurjc.mokaf.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record BranchDTO(
        Long id,
        @NotBlank String name,
        String description,
        @NotNull BigDecimal purchaseDiscountPercent
) {}