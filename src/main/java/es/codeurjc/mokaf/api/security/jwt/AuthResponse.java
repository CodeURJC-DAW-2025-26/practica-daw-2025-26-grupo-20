package es.codeurjc.mokaf.api.security.jwt;


public record AuthResponse(Status status, String message) {

    public enum Status { SUCCESS, FAILURE }
}
