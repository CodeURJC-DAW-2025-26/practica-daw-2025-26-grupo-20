package es.codeurjc.mokaf.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDTO(
        // ── Identity ──────────────────────────────────────────────────────
        Long id,
        String name,
        String email,
        String role,

        // ── Password (solo escritura, nunca se devuelve en la respuesta) ──
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,

        // ── Image ─────────────────────────────────────────────────────────
        Long imageId,
        String profileImageUrl,

        // ── Personal data ───────────────────────────────────────────────
        String firstName,
        String lastName,
        String description,

        // ── Laboral data (relevant for ADMIN / EMPLOYEE) ─────────────
        String position,
        String department,
        BigDecimal salary,
        LocalDateTime hireDate,

        // ── Metadata ──────────────────────────────────────────────────────
        LocalDateTime createdAt
) {}