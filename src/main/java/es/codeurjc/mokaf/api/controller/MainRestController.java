package es.codeurjc.mokaf.api.controller;

import es.codeurjc.mokaf.api.dto.ContactRequestDTO;
import es.codeurjc.mokaf.api.dto.UserDTO;
import es.codeurjc.mokaf.api.mapper.UserMapper;
import es.codeurjc.mokaf.model.ContactRequest;
import es.codeurjc.mokaf.service.BranchService;
import es.codeurjc.mokaf.service.ContactEmailService;
import es.codeurjc.mokaf.service.OrdersService;
import es.codeurjc.mokaf.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class MainRestController {

    private final UserService userService;
    private final ContactEmailService contactEmailService;
    private final UserMapper userMapper;

    public MainRestController(OrdersService ordersService,
            UserService userService,
            BranchService branchService,
            ContactEmailService contactEmailService,
            UserMapper userMapper) {
        this.userService = userService;
        this.contactEmailService = contactEmailService;
        this.userMapper = userMapper;
    }

    // ── POST /api/v1/contact ──────────────────────────────────────────────────
    @Operation(summary = "Send a contact message")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Message sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping(value = "/contact", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> sendContactMessage(@Valid @RequestBody ContactRequestDTO dto) {

        ContactRequest request = new ContactRequest();
        request.setFirstName(dto.firstName());
        request.setLastName(dto.lastName());
        request.setEmail(dto.email());
        request.setPhone(dto.phone());
        request.setSubject(dto.subject());
        request.setMessage(dto.message());
        request.setNewsletter(dto.newsletter());

        contactEmailService.sendContactEmail(request);

        return Map.of("message", "Mensaje enviado correctamente");
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

}