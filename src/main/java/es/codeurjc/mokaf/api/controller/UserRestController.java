package es.codeurjc.mokaf.api.controller;

import es.codeurjc.mokaf.api.dto.UserDTO;
import es.codeurjc.mokaf.api.exception.ResourceNotFoundException;
import es.codeurjc.mokaf.api.exception.UnauthorizedException;
import es.codeurjc.mokaf.api.mapper.UserMapper;
import es.codeurjc.mokaf.model.Image;
import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.service.ImageService;
import es.codeurjc.mokaf.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * API REST de usuarios.
 *
 * ── Perfil propio (cualquier usuario autenticado) ──────────────────────────
 *   GET    /api/v1/users/me              → Ver mi perfil
 *   PUT    /api/v1/users/me              → Actualizar mis datos
 *   POST   /api/v1/users/me/image        → Subir/cambiar mi foto de perfil
 *   DELETE /api/v1/users/me              → Eliminar mi cuenta
 *
 * ── Gestión de usuarios (operaciones generales) ────────────────────────────
 *   GET    /api/v1/users                 → Listar todos los usuarios
 *   GET    /api/v1/users/{id}            → Ver usuario por id
 *   POST   /api/v1/users                 → Crear usuario
 *   PUT    /api/v1/users/{id}            → Editar usuario por id
 *   DELETE /api/v1/users/{id}            → Eliminar usuario por id
 *
 * Admins y customers son el mismo tipo (User) diferenciados por rol.
 * Seguridad: sesión HTTP compartida con la web MVC. CSRF desactivado para /api/**.
 */
@Tag(name = "Users", description = "User management and profile operations")
@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ImageService imageService;

    // =========================================================================
    // PERFIL PROPIO  —  /api/v1/users/me
    // =========================================================================

    @Operation(summary = "Get the authenticated user's profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile returned successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping("/me")
    public UserDTO getMe(HttpServletRequest request) {
        return userMapper.toDTO(resolveCurrentUser(request));
    }

    // -------------------------------------------------------------------------

    @Operation(summary = "Update the authenticated user's profile",
               description = "Updates name, email, password and any personal/work fields. "
                           + "All params except name and email are optional. "
                           + "Works for both CUSTOMER and ADMIN roles.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    @PutMapping("/me")
    public UserDTO updateMe(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String department,
            HttpServletRequest request) {

        User user = resolveCurrentUser(request);

        if (!email.equals(user.getEmail()) && userService.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use: " + email);
        }

        user.setName(name);
        user.setEmail(email);
        if (firstName   != null) user.setFirstName(firstName);
        if (lastName    != null) user.setLastName(lastName);
        if (description != null) user.setDescription(description);
        if (position    != null) user.setPosition(position);
        if (department  != null) user.setDepartment(department);

        if (password != null && !password.isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(password));
        }

        User saved = userService.save(user);
        refreshSecurityContext(saved, request);

        return userMapper.toDTO(saved);
    }

    // -------------------------------------------------------------------------

    @Operation(summary = "Upload or replace the authenticated user's profile image",
               description = "Accepts multipart/form-data with a field named 'image'.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image updated successfully"),
            @ApiResponse(responseCode = "400", description = "No image file provided"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> uploadMyImage(
            @RequestParam("image") MultipartFile imageFile,
            HttpServletRequest request) {

        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("No image file provided");
        }

        User user = resolveCurrentUser(request);

        try {
            Image newImage = imageService.updateImage(
                    user.getImage() != null ? user.getImage().getId() : null,
                    imageFile);

            if (newImage == null) {
                throw new RuntimeException("Could not save the image");
            }

            user.setImage(newImage);
            User saved = userService.save(user);
            refreshSecurityContext(saved, request);

            return Map.of(
                    "message", "Image updated successfully",
                    "profileImageUrl", saved.getProfileImageUrl());

        } catch (IOException e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage(), e);
        }
    }

    // -------------------------------------------------------------------------

    @Operation(summary = "Delete the authenticated user's own account",
               description = "Permanently deletes the account and invalidates the current session.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMe(HttpServletRequest request, HttpServletResponse response) {

        User user = resolveCurrentUser(request);

        if (user.getImage() != null) {
            imageService.deleteImage(user.getImage().getId());
        }

        userService.delete(user);

        new SecurityContextLogoutHandler().logout(
                request, response,
                SecurityContextHolder.getContext().getAuthentication());
    }

    // =========================================================================
    // GESTIÓN DE USUARIOS  —  /api/v1/users  y  /api/v1/users/{id}
    // =========================================================================

    @Operation(summary = "Get all users")
    @GetMapping
    public List<UserDTO> getUsers() {
        return userService.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------

    @Operation(summary = "Get a user by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
    }

    // -------------------------------------------------------------------------

    @Operation(summary = "Create a new user")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@Valid @RequestBody UserDTO userDTO) {
        if (userService.existsByEmail(userDTO.email())) {
            throw new IllegalArgumentException("Email already in use: " + userDTO.email());
        }

        User user = userMapper.toEntity(userDTO);
        if (user.getRole() == null) {
            user.setRole(User.Role.CUSTOMER);
        }

        return userMapper.toDTO(userService.save(user));
    }

    // -------------------------------------------------------------------------

    @Operation(summary = "Update a user by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        return userService.findById(id).map(user -> {
            if (userDTO.email() != null && !userDTO.email().equals(user.getEmail())) {
                if (userService.existsByEmail(userDTO.email())) {
                    throw new IllegalArgumentException("Email already in use: " + userDTO.email());
                }
            }
            userMapper.updateEntity(user, userDTO);
            return userMapper.toDTO(userService.save(user));
        }).orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
    }

    // -------------------------------------------------------------------------

    @Operation(summary = "Delete a user by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        if (user.getImage() != null) {
            imageService.deleteImage(user.getImage().getId());
        }

        userService.delete(user);
    }

    // =========================================================================
    // Helpers privados
    // =========================================================================

    /**
     * Obtiene el User autenticado desde el principal de la sesión HTTP.
     * Lanza UnauthorizedException si no hay sesión activa.
     */
    private User resolveCurrentUser(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            throw new UnauthorizedException("No authenticated user found");
        }
        return userService.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Logged user data not found in database"));
    }

    /**
     * Refresca el SecurityContext en la sesión HTTP tras actualizar el perfil,
     * para que la parte MVC (navbar, profileADMIN, etc.) vea los datos nuevos.
     */
    private void refreshSecurityContext(User updatedUser, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                updatedUser, null, updatedUser.getAuthorities());
        newAuth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(newAuth);

        request.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());
    }
}