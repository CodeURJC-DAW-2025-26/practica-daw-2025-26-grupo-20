package es.codeurjc.mokaf.api.dto;

import es.codeurjc.mokaf.model.Faq;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FaqDTO(
    Long id,
    @NotBlank(message = "Question is required")
    @Size(max = 255)
    String question,
    @NotBlank(message = "Answer is required")
    String answer
) {
}
