package es.codeurjc.mokaf.api.controller;

import es.codeurjc.mokaf.api.dto.UserDTO;
import es.codeurjc.mokaf.api.mapper.UserMapper;
import es.codeurjc.mokaf.api.security.jwt.AuthResponse;
import es.codeurjc.mokaf.api.security.jwt.LoginRequest;
import es.codeurjc.mokaf.api.security.jwt.UserLoginService;
import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication endpoints (all public).
 *
 *   POST   /api/v1/auth/sessions         → login, returns JWT cookies
 *   POST   /api/v1/auth/registrations    → register new CUSTOMER account
 *   POST   /api/v1/auth/tokens           → renew AuthToken using RefreshToken cookie
 *   DELETE /api/v1/auth/sessions/current → clear JWT cookies
 */
@Tag(name = "Auth", description = "Authentication and registration endpoints")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthRestController {

    @Autowired private UserLoginService userLoginService;
    @Autowired private UserService userService;
    @Autowired private UserMapper userMapper;
    @Autowired private PasswordEncoder passwordEncoder;

    // ── Signup request DTO ────────────────────────────────────────────────────

    public record SignupRequest(
            @NotBlank(message = "Name is required")
            String name,

            @NotBlank(message = "Email is required")
            @Email(message = "Email format is invalid")
            String email,

            @NotBlank(message = "Password is required")
            @Size(min = 6, message = "Password must be at least 6 characters")
            @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$",
                message = "Password must contain both letters and numbers"
            )
            String password
    ) {}

    // ── Endpoints ─────────────────────────────────────────────────────────────

    @Operation(summary = "Login with email and password",
               description = "Returns HttpOnly cookies: AuthToken (5 min) and RefreshToken (7 days). "
                           + "Subtítulo vídeo: 'Endpoint de login'")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/sessions")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        return userLoginService.login(response, loginRequest);
    }

    @Operation(summary = "Register a new user account",
               description = "Creates a new CUSTOMER account. "
                           + "Subtítulo vídeo: 'Endpoint de registro'")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or email already in use")
    })
    @PostMapping("/registrations")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO signup(@Valid @RequestBody SignupRequest request) {
        if (userService.existsByEmail(request.email()))
            throw new IllegalArgumentException("Email already registered: " + request.email());

        User newUser = new User();
        newUser.setName(request.name());
        newUser.setEmail(request.email());
        newUser.setPasswordHash(passwordEncoder.encode(request.password()));
        newUser.setRole(User.Role.CUSTOMER);

        return userMapper.toDTO(userService.save(newUser));
    }

    @Operation(summary = "Refresh the access token",
               description = "Uses the RefreshToken cookie to issue a new AuthToken cookie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token refreshed"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    @PostMapping("/tokens")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = "RefreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        return userLoginService.refresh(response, refreshToken);
    }

    @Operation(summary = "Logout — clears JWT cookies")
    @DeleteMapping("/sessions/current")
    public ResponseEntity<AuthResponse> logout(HttpServletResponse response) {
        return userLoginService.logout(response);
    }
}
