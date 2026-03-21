package es.codeurjc.mokaf.api.dto.CartDTOs;


public record CartResponseDTO(
    Boolean success,
    String message,
    CartSummaryDTO cart,
    Integer cartCount,
    String redirect
) {}