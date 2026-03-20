package es.codeurjc.mokaf.api.security.jwt;

import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Lógica de autenticación JWT:
 *  - login  : valida credenciales, genera ACCESS + REFRESH token como cookies
 *  - refresh: valida el RefreshToken y emite un nuevo AccessToken
 *  - logout : borra ambas cookies
 */
@Service
public class UserLoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    // ── Login ─────────────────────────────────────────────────────────────────

    public ResponseEntity<AuthResponse> login(HttpServletResponse response,
                                              LoginRequest loginRequest) {
        try {
            // Delegar en Spring Security para validar email + password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()));

            User user = (User) authentication.getPrincipal();
            String role = user.getRole().name();

            // Generar ACCESS token (5 min) y REFRESH token (7 días)
            String accessToken  = jwtTokenProvider.generateToken(user.getEmail(), role, TokenType.ACCESS);
            String refreshToken = jwtTokenProvider.generateToken(user.getEmail(), role, TokenType.REFRESH);

            // Escribir como cookies HttpOnly
            jwtTokenProvider.setTokenCookie(response, accessToken,  TokenType.ACCESS);
            jwtTokenProvider.setTokenCookie(response, refreshToken, TokenType.REFRESH);

            return ResponseEntity.ok(
                    new AuthResponse(AuthResponse.Status.SUCCESS, "Login successful"));

        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(new AuthResponse(AuthResponse.Status.FAILURE, "Invalid credentials"));
        }
    }

    // ── Refresh ───────────────────────────────────────────────────────────────

    public ResponseEntity<AuthResponse> refresh(HttpServletResponse response,
                                                String refreshToken) {
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(401)
                    .body(new AuthResponse(AuthResponse.Status.FAILURE, "Invalid or expired refresh token"));
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        String role     = jwtTokenProvider.getRoleFromToken(refreshToken);

        // Verificar que el usuario sigue existiendo en base de datos
        User user = userService.findByEmail(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401)
                    .body(new AuthResponse(AuthResponse.Status.FAILURE, "User not found"));
        }

        // Emitir nuevo AccessToken
        String newAccessToken = jwtTokenProvider.generateToken(username, role, TokenType.ACCESS);
        jwtTokenProvider.setTokenCookie(response, newAccessToken, TokenType.ACCESS);

        return ResponseEntity.ok(
                new AuthResponse(AuthResponse.Status.SUCCESS, "Token refreshed"));
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    public ResponseEntity<AuthResponse> logout(HttpServletResponse response) {
        jwtTokenProvider.clearTokenCookie(response, TokenType.ACCESS);
        jwtTokenProvider.clearTokenCookie(response, TokenType.REFRESH);

        return ResponseEntity.ok(
                new AuthResponse(AuthResponse.Status.SUCCESS, "Logout successful"));
    }
}
