package es.codeurjc.mokaf.api.dto.CartDTOs;

import java.util.List;

public record CartSummaryDTO(
    List<CartItemDTO> items,
    String subtotal,
    String tax,
    String total,
    String discountInfo,
    Boolean hasDiscount,
    Integer itemCount,
    Integer totalUnits
) {}
