package es.codeurjc.mokaf.api.dto.cartdtos;

import java.math.BigDecimal;

public record CartItemDTO(
    Long id,
    Long productId,
    String name,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal lineTotal,
    String imageUrl
) {}
