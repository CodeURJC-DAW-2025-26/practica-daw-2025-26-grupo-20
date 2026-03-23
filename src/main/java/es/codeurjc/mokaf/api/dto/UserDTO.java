package es.codeurjc.mokaf.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record UserDTO(
        // ── Identity ──────────────────────────────────────────────────────
        Long id,
        String name,
        String email,
        String role,

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
