package es.codeurjc.mokaf.api.dto;

import java.math.BigDecimal;

public record OrderItemDTO(
        Long id,
        Long productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal finalUnitPrice,
        BigDecimal lineTotal
) {}