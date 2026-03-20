package es.codeurjc.mokaf.api.controller;

import es.codeurjc.mokaf.api.dto.LoginRequestDTO;
import es.codeurjc.mokaf.api.dto.UserDTO;
import es.codeurjc.mokaf.api.exception.UnauthorizedException;
import es.codeurjc.mokaf.api.mapper.UserMapper;
import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthRestController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final UserService userService;
    private final UserMapper userMapper;

    public AuthRestController(AuthenticationManager authenticationManager,
                              SecurityContextRepository securityContextRepository,
                              UserService userService,
                              UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    // ── POST /api/v1/auth/login ───────────────────────────────────────────────
    @Operation(summary = "Login and start a session")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public UserDTO login(@Valid @RequestBody LoginRequestDTO loginRequest,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        try {
            // Autenticar credenciales
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()
                    )
            );

            // Guardar en el contexto de seguridad y en la sesión (cookie JSESSIONID)
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            // Devolver datos del usuario autenticado
            User user = userService.findByEmail(loginRequest.email())
                    .orElseThrow(() -> new UnauthorizedException("User not found"));

            return userMapper.toDTO(user);

        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid email or password");
        }
    }

    // ── POST /api/v1/auth/logout ──────────────────────────────────────────────
    @Operation(summary = "Logout and invalidate session")
    @ApiResponse(responseCode = "204", description = "Logged out successfully")
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) {
        request.getSession(false); // no crear sesión nueva
        var session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }

    // ── GET /api/v1/auth/me ───────────────────────────────────────────────────
    @Operation(summary = "Get current authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authenticated user info"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping("/me")
    public UserDTO me(HttpServletRequest request) {
        var principal = request.getUserPrincipal();
        if (principal == null) {
            throw new UnauthorizedException("Not authenticated");
        }
        return userService.findByEmail(principal.getName())
                .map(userMapper::toDTO)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

}