package es.codeurjc.mokaf.api.security.jwt;

/** DTO de entrada para el login REST */
public record LoginRequest(String email, String password) {}
