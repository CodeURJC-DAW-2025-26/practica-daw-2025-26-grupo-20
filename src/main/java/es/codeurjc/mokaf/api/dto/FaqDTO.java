package es.codeurjc.mokaf.api.dto;

public record FaqDTO(
        Long id,
        String question,
        String answer
) {
}