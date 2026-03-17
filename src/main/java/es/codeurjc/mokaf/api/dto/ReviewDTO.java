package es.codeurjc.mokaf.api.dto;

public record ReviewDTO(
        Long id,
        UserBasicDTO user,
        int stars,
        String text,
        String createdAtFormatted,
        Long productId) {
}