package es.codeurjc.mokaf.api.security.jwt;

/** DTO de respuesta para las operaciones de autenticación */
public record AuthResponse(Status status, String message) {

    public enum Status { SUCCESS, FAILURE }
}
