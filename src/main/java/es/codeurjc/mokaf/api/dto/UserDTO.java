package es.codeurjc.mokaf.api.dto;

import es.codeurjc.mokaf.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record UserDTO(
    Long id,
    @NotBlank(message = "Name is required")
    @Size(max = 120)
    String name,
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,
    String role,
    Long imageId,
    LocalDateTime createdAt
) {
}
