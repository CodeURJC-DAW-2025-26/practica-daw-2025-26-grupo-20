package es.codeurjc.mokaf.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ContactRequestDTO(
        @NotBlank String firstName,
        String lastName,
        @NotBlank @Email String email,
        String phone,
        @NotBlank String subject,
        @NotBlank String message,
        boolean newsletter
) {}