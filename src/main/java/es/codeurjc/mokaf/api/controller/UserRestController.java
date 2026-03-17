package es.codeurjc.mokaf.api.controller;

import es.codeurjc.mokaf.api.dto.UserDTO;
import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Get all users")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsers() {
        List<User> users = userService.findAll();
        List<UserDTO> dtos = users.stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Get current authenticated user")
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMe(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        return userService.findByEmail(principal.getName())
                .map(user -> ResponseEntity.ok(new UserDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get a user by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(new UserDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new user")
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        if (userService.existsByEmail(userDTO.getEmail())) {
            return ResponseEntity.badRequest().build();
        }

        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        // For security, password should be set via a dedicated registration or password-reset flow
        // or a default temporal password could be set here.
        user.setRole(userDTO.getRole() != null ? User.Role.valueOf(userDTO.getRole()) : User.Role.CUSTOMER);

        User savedUser = userService.save(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedUser.getId()).toUri();

        return ResponseEntity.created(location).body(new UserDTO(savedUser));
    }

    @Operation(summary = "Update an existing user")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return userService.findById(id).map(user -> {
            user.setName(userDTO.getName());
            if (userDTO.getRole() != null) {
                try {
                    user.setRole(User.Role.valueOf(userDTO.getRole()));
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().<UserDTO>build();
                }
            }
            // Email update might need more verification, but adding here
            if (userDTO.getEmail() != null && !userDTO.getEmail().equals(user.getEmail())) {
                if (userService.existsByEmail(userDTO.getEmail())) {
                    return ResponseEntity.badRequest().<UserDTO>build();
                }
                user.setEmail(userDTO.getEmail());
            }

            userService.save(user);
            return ResponseEntity.ok(new UserDTO(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a user")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        return userService.findById(id).map(user -> {
            userService.delete(user);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
