package es.codeurjc.mokaf.api.controller;

import es.codeurjc.mokaf.api.dto.UserDTO;
import es.codeurjc.mokaf.api.mapper.UserMapper;
import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.service.UserService;
import es.codeurjc.mokaf.api.exception.ResourceNotFoundException;
import es.codeurjc.mokaf.api.exception.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Operation(summary = "Get all users")
    @GetMapping
    public List<UserDTO> getUsers() {
        return userService.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get current authenticated user")
    @GetMapping("/me")
    public UserDTO getMe(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            throw new UnauthorizedException("No authenticated user found");
        }
        return userService.findByEmail(principal.getName())
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Logged user data not found in database"));
    }

    @Operation(summary = "Get a user by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
    }

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

        User savedUser = userService.save(user);
        return userMapper.toDTO(savedUser);
    }

    @Operation(summary = "Update an existing user")
    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        return userService.findById(id).map(user -> {
            if (userDTO.email() != null && !userDTO.email().equals(user.getEmail())) {
                if (userService.existsByEmail(userDTO.email())) {
                    throw new IllegalArgumentException("Email already in use: " + userDTO.email());
                }
            }

            userMapper.updateEntity(user, userDTO);
            userService.save(user);
            return userMapper.toDTO(user);
        }).orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
    }

    @Operation(summary = "Delete a user")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        userService.delete(user);
    }
}
