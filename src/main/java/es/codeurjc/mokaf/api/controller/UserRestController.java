package es.codeurjc.mokaf.api.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

@Tag(name = "Users", description = "User management and profile operations")
@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    @Autowired private UserService userService;
    @Autowired private UserMapper userMapper;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ImageService imageService;

    // ── Request body for profile update ──────────────────────────────────────

    /**
     * JSON body for PUT /api/v1/users/me
     * All fields are optional except name and email.
     * password is only updated if provided.
     */
    public record UpdateMeRequest(
            String name,
            String email,
            String password,
            String firstName,
            String lastName,
            String description,
            String position,
            String department
    ) {}

    // ── Own profile ───────────────────────────────────────────────────────────

    @Operation(summary = "Get the authenticated user's profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile returned successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping("/me")
    public UserDTO getMe(HttpServletRequest request) {
        return userMapper.toDTO(resolveCurrentUser(request));
    }

    @Operation(summary = "Update the authenticated user's own profile",
               description = "Accepts JSON body. All fields optional except name and email. "
                           + "password only updated if provided.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    @PutMapping("/me")
    public UserDTO updateMe(
            @RequestBody UpdateMeRequest body,
            HttpServletRequest request) {

        User user = resolveCurrentUser(request);

        if (body.email() != null && !body.email().equals(user.getEmail())
                && userService.existsByEmail(body.email()))
            throw new IllegalArgumentException("Email already in use: " + body.email());

        if (body.name()        != null) user.setName(body.name());
        if (body.email()       != null) user.setEmail(body.email());
        if (body.firstName()   != null) user.setFirstName(body.firstName());
        if (body.lastName()    != null) user.setLastName(body.lastName());
        if (body.description() != null) user.setDescription(body.description());
        if (body.position()    != null) user.setPosition(body.position());
        if (body.department()  != null) user.setDepartment(body.department());
        if (body.password() != null && !body.password().isBlank())
            user.setPasswordHash(passwordEncoder.encode(body.password()));

        User saved = userService.save(user);
        refreshSecurityContext(saved, request);
        return userMapper.toDTO(saved);
    }

    @Operation(summary = "Upload or replace profile image",
               description = "Accepts multipart/form-data with field 'image'. "
                           + "Subtítulo vídeo: 'Endpoint imagen User entidad'")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image updated"),
            @ApiResponse(responseCode = "400", description = "No image provided"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> uploadMyImage(
            @RequestParam("image") MultipartFile imageFile,
            HttpServletRequest request) {

        if (imageFile == null || imageFile.isEmpty())
            throw new IllegalArgumentException("No image file provided");

        User user = resolveCurrentUser(request);
        try {
            Image newImage = imageService.updateImage(
                    user.getImage() != null ? user.getImage().getId() : null, imageFile);
            if (newImage == null) throw new RuntimeException("Could not save image");
            user.setImage(newImage);
            User saved = userService.save(user);
            refreshSecurityContext(saved, request);
            return Map.of("message", "Image updated successfully",
                          "profileImageUrl", saved.getProfileImageUrl());
        } catch (IOException e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage(), e);
        }
    }

    @Operation(summary = "Delete the authenticated user's own account")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account deleted"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMe(HttpServletRequest request, HttpServletResponse response) {
        User user = resolveCurrentUser(request);
        if (user.getImage() != null) imageService.deleteImage(user.getImage().getId());
        userService.delete(user);
        new SecurityContextLogoutHandler().logout(
                request, response, SecurityContextHolder.getContext().getAuthentication());
    }

    // ── User management (ADMIN) ───────────────────────────────────────────────

    @Operation(summary = "List all users",
               description = "Subtítulo vídeo: 'Endpoint paginado de User'")
    @GetMapping
    public List<UserDTO> getUsers() {
        return userService.findAll().stream().map(userMapper::toDTO).collect(Collectors.toList());
    }

    @Operation(summary = "Get a user by id",
               description = "Subtítulo vídeo: 'Endpoint detalle de User'")
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
    @Operation(summary = "Create a new user",
               description = "Subtítulo vídeo: 'Endpoint creación de User'")
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        if (userService.existsByEmail(userDTO.email()))
            throw new IllegalArgumentException("Email already in use: " + userDTO.email());
        
        // Validar que venga password
        if (userDTO.password() == null || userDTO.password().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        
        User user = userMapper.toEntity(userDTO);
        
        // CODIFICAR EL PASSWORD antes de guardar (esto faltaba)
        user.setPasswordHash(passwordEncoder.encode(userDTO.password()));
        
        if (user.getRole() == null) user.setRole(User.Role.CUSTOMER);
        User saved = userService.save(user);
        UserDTO savedDto = userMapper.toDTO(saved);
    
        return ResponseEntity.created(
                        ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(saved.getId())
                                .toUri())
                .body(savedDto);
    }

    @Operation(summary = "Update a user by id",
               description = "ADMIN: any user. Non-admin: own profile only. "
                           + "Subtítulo vídeo: 'Endpoint control de acceso por dueño'")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "403", description = "Access denied — not the owner"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    public UserDTO updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO,
            HttpServletRequest request) {

        User currentUser = resolveCurrentUser(request);
        User target = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        if (currentUser.getRole() != User.Role.ADMIN
                && !currentUser.getId().equals(target.getId()))
            throw new UnauthorizedException("Access denied: you can only edit your own profile");

        if (userDTO.email() != null && !userDTO.email().equals(target.getEmail())
                && userService.existsByEmail(userDTO.email()))
            throw new IllegalArgumentException("Email already in use: " + userDTO.email());

        userMapper.updateEntity(target, userDTO);
        return userMapper.toDTO(userService.save(target));
    }

    @Operation(summary = "Delete a user by id",
               description = "ADMIN: any user. Non-admin: own account only. "
                           + "Subtítulo vídeo: 'Endpoint control de acceso por dueño'")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "403", description = "Access denied — not the owner"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = resolveCurrentUser(request);
        User target = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        if (currentUser.getRole() != User.Role.ADMIN
                && !currentUser.getId().equals(target.getId()))
            throw new UnauthorizedException("Access denied: you can only delete your own account");

        if (target.getImage() != null) imageService.deleteImage(target.getImage().getId());
        userService.delete(target);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private User resolveCurrentUser(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal == null) throw new UnauthorizedException("No authenticated user found");
        return userService.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Logged user not found in database"));
    }

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