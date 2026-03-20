package es.codeurjc.mokaf.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO unificado para lectura y actualización de usuarios/perfiles.
 *
 * Lectura  (GET)  → todos los campos se rellenan desde la entidad.
 * Escritura (PUT) → solo se usan los campos no nulos para actualizar.
 *
 * La contraseña y la imagen se gestionan aparte:
 *   - password : solo en PUT /api/v1/users/me y PUT /api/v1/users/{id}  (RequestParam)
 *   - image    : solo en POST /api/v1/users/me/image  (multipart)
 */
public record UserDTO(
        // ── Identidad ──────────────────────────────────────────────────────
        Long id,
        String name,
        String email,
        String role,

        // ── Imagen ─────────────────────────────────────────────────────────
        Long imageId,
        String profileImageUrl,

        // ── Datos personales ───────────────────────────────────────────────
        String firstName,
        String lastName,
        String description,

        // ── Datos laborales (relevantes para ADMIN / EMPLOYEE) ─────────────
        String position,
        String department,
        BigDecimal salary,
        LocalDateTime hireDate,

        // ── Metadatos ──────────────────────────────────────────────────────
        LocalDateTime createdAt
) {}
