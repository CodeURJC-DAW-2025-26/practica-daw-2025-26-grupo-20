package es.codeurjc.mokaf.api.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Genera, valida y extrae información de tokens JWT.
 * Los tokens viajan en cookies HttpOnly (más seguro que cabeceras para SPA).
 *
 * Configura el secreto en application.properties:
 *   jwt.secret=clave-secreta-larga-de-al-menos-32-caracteres
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret:mokaf-secret-key-must-be-at-least-32-chars-long}")
    private String jwtSecret;

    // ── Generación ────────────────────────────────────────────────────────────

    /**
     * Genera un token JWT (ACCESS o REFRESH) para el usuario dado
     * y lo establece como cookie HttpOnly en la respuesta.
     */
    public String generateToken(String username, String role, TokenType type) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + type.getDuration().toMillis());

        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getKey())
                .compact();
    }

    /**
     * Escribe el token como cookie HttpOnly en la respuesta HTTP.
     */
    public void setTokenCookie(HttpServletResponse response, String token, TokenType type) {
        Cookie cookie = new Cookie(type.getCookieName(), token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);        // Solo HTTPS
        cookie.setPath("/");
        cookie.setMaxAge((int) type.getDuration().toSeconds());
        response.addCookie(cookie);
    }

    /**
     * Borra la cookie del token (logout).
     */
    public void clearTokenCookie(HttpServletResponse response, TokenType type) {
        Cookie cookie = new Cookie(type.getCookieName(), "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);           // Expira inmediatamente
        response.addCookie(cookie);
    }

    // ── Lectura ───────────────────────────────────────────────────────────────

    /**
     * Extrae el token JWT de la cookie correspondiente al tipo dado.
     */
    public String getTokenFromCookie(HttpServletRequest request, TokenType type) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (type.getCookieName().equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * Extrae el username (email) del token.
     */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Extrae el rol del token.
     */
    public String getRoleFromToken(String token) {
        return parseClaims(token).get("role", String.class);
    }

    // ── Validación ────────────────────────────────────────────────────────────

    /**
     * Devuelve true si el token es válido (firma correcta y no caducado).
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("JWT expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.warn("JWT unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.warn("JWT malformed: {}", e.getMessage());
        } catch (SecurityException e) {
            logger.warn("JWT signature invalid: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.warn("JWT empty: {}", e.getMessage());
        }
        return false;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
