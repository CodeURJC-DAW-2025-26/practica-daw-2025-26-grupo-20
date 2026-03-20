package es.codeurjc.mokaf.api.controller;

import es.codeurjc.mokaf.api.dto.BranchDTO;
import es.codeurjc.mokaf.api.dto.ContactRequestDTO;
import es.codeurjc.mokaf.api.dto.OrderDTO;
import es.codeurjc.mokaf.api.dto.UserDTO;
import es.codeurjc.mokaf.api.exception.ResourceNotFoundException;
import es.codeurjc.mokaf.api.mapper.BranchMapper;
import es.codeurjc.mokaf.api.mapper.OrderMapper;
import es.codeurjc.mokaf.api.mapper.UserMapper;
import es.codeurjc.mokaf.model.ContactRequest;
import es.codeurjc.mokaf.model.Order;
import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.service.BranchService;
import es.codeurjc.mokaf.service.ContactEmailService;
import es.codeurjc.mokaf.service.OrdersService;
import es.codeurjc.mokaf.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class MainRestController {

    private final OrdersService ordersService;
    private final UserService userService;
    private final BranchService branchService;
    private final ContactEmailService contactEmailService;
    private final OrderMapper orderMapper;
    private final BranchMapper branchMapper;
    private final UserMapper userMapper;

    public MainRestController(OrdersService ordersService,
                              UserService userService,
                              BranchService branchService,
                              ContactEmailService contactEmailService,
                              OrderMapper orderMapper,
                              BranchMapper branchMapper,UserMapper userMapper) {
        this.ordersService = ordersService;
        this.userService = userService;
        this.branchService = branchService;
        this.contactEmailService = contactEmailService;
        this.orderMapper = orderMapper;
        this.branchMapper = branchMapper;
        this.userMapper = userMapper;
    }


    // ── POST /api/v1/contact ──────────────────────────────────────────────────
    // Cualquiera puede enviar un mensaje de contacto
    @Operation(summary = "Send a contact message")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Message sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping("/contact")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sendContactMessage(@Valid @RequestBody ContactRequestDTO dto) {

        ContactRequest request = new ContactRequest();
        request.setFirstName(dto.firstName());
        request.setLastName(dto.lastName());
        request.setEmail(dto.email());
        request.setPhone(dto.phone());
        request.setSubject(dto.subject());
        request.setMessage(dto.message());
        request.setNewsletter(dto.newsletter());

        contactEmailService.sendContactEmail(request);
    }

    // ── GET /api/v1/about-us ──────────────────────────────────────────────────
    // Devuelve el equipo de atención al cliente 
    @Operation(summary = "Get customer service team (about us)")
    @GetMapping("/about-us")
    public List<UserDTO> getAboutUsTeam() {
        return userService.getStaffByDepartment("Atencion al cliente").stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
    // ── Helper ────────────────────────────────────────────────────────────────
    private User resolveUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User u) {
            return u;
        }

        if (principal instanceof String email) {
            return userService.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED, "User not found"));
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
    }
}