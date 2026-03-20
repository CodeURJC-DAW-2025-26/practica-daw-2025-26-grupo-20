package es.codeurjc.mokaf.api.security.jwt;

import java.time.Duration;

/**
 * Define los dos tipos de token JWT y su duración.
 * ACCESS caduca pronto (5 min) — se renueva con REFRESH.
 * REFRESH dura más (7 días) — se usa solo para pedir un nuevo ACCESS.
 */
public enum TokenType {

    ACCESS (Duration.ofMinutes(5),  "AuthToken"),
    REFRESH(Duration.ofDays(7),     "RefreshToken");

    private final Duration duration;
    private final String cookieName;

    TokenType(Duration duration, String cookieName) {
        this.duration   = duration;
        this.cookieName = cookieName;
    }

    public Duration getDuration()  { return duration; }
    public String   getCookieName(){ return cookieName; }
}
