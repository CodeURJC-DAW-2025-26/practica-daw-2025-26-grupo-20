package es.codeurjc.mokaf.api.security.jwt;

import java.time.Duration;


public enum TokenType {

    ACCESS (Duration.ofMinutes(30),  "AuthToken"),
    REFRESH(Duration.ofDays(14),     "RefreshToken");

    private final Duration duration;
    private final String cookieName;

    TokenType(Duration duration, String cookieName) {
        this.duration   = duration;
        this.cookieName = cookieName;
    }

    public Duration getDuration()  { return duration; }
    public String   getCookieName(){ return cookieName; }
}
