package es.codeurjc.mokaf.api.dto;

public record BranchDTO(
        Long id,
        String name,
        String address,
        String city,
        String phone
) {}