package es.codeurjc.mokaf.api.dto;

import java.time.LocalDateTime;

public record UserDTO(
        Long id,
        String name,
        String email,
        String role,
        Long imageId,
        LocalDateTime createdAt
) {
}
