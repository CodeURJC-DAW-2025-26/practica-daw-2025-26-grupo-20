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


@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret:mokaf-secret-key-must-be-at-least-32-chars-long}")
    private String jwtSecret;

    // ── Generación ────────────────────────────────────────────────────────────

    /**
     * Generate a JWT token containing username and role, with expiration based on the token type (ACCESS or REFRESH).
     * set the role as a claim in the token, so we can use it in the filter without needing to query the database again.
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
     * Set the JWT token in an HttpOnly cookie in the response. The cookie name and duration depend on the token type (ACCESS or REFRESH).
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
     * Erase the JWT cookie by setting an empty value and maxAge=0. The cookie name depends on the token type (ACCESS or REFRESH).
     */
    public void clearTokenCookie(HttpServletResponse response, TokenType type) {
        Cookie cookie = new Cookie(type.getCookieName(), "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);           // Expires immediately
        response.addCookie(cookie);
    }

    // ── Lectura ───────────────────────────────────────────────────────────────

    /**
     * Extract the JWT token from the request cookies. The cookie name depends on the token type (ACCESS or REFRESH).
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
     * Extract the username (subject) from the token claims. Assumes the token is valid.
     */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Extract the role from the token claims. Assumes the token is valid. The role is stored as a custom claim named "role".
     */
    public String getRoleFromToken(String token) {
        return parseClaims(token).get("role", String.class);
    }

    // ── Validación ────────────────────────────────────────────────────────────

    /**
     * return true if the token is valid (signature correct and not expired), false otherwise. Logs the reason for invalidity (expired, malformed, etc.)
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
