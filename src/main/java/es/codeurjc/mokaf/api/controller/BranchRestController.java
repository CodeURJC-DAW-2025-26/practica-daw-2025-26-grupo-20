package es.codeurjc.mokaf.api.controller;

import es.codeurjc.mokaf.api.dto.BranchDTO;
import es.codeurjc.mokaf.api.exception.ResourceNotFoundException;
import es.codeurjc.mokaf.api.mapper.BranchMapper;
import es.codeurjc.mokaf.model.Branch;
import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.service.BranchService;
import es.codeurjc.mokaf.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/branches")
public class BranchRestController {

    private final BranchService branchService;
    private final BranchMapper branchMapper;
    private final UserService userService;

    public BranchRestController(BranchService branchService,
                                BranchMapper branchMapper,
                                UserService userService) {
        this.branchService = branchService;
        this.branchMapper = branchMapper;
        this.userService = userService;
    }

    // ── GET /api/v1/branches ──────────────────────────────────────────────────

    @Operation(summary = "Get all branches")
    @GetMapping
    public List<BranchDTO> getAllBranches() {
        return branchService.getAllBranches().stream()
                .map(branchMapper::toDto)
                .collect(Collectors.toList());
    }

    // ── GET /api/v1/branches/{id} ─────────────────────────────────────────────
    
    @Operation(summary = "Get a branch by its id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Branch found"),
            @ApiResponse(responseCode = "404", description = "Branch not found")
    })
    @GetMapping("/{id}")
    public BranchDTO getBranch(@PathVariable Long id) {
        return branchService.getBranchById(id)
                .map(branchMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found: " + id));
    }

    // ── POST /api/v1/branches ─────────────────────────────────────────────────
   
    @Operation(summary = "Create a new branch. Admin only.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Branch created"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Forbidden — admin only")
    })
    @PostMapping
    public ResponseEntity<BranchDTO> createBranch(@Valid @RequestBody BranchDTO branchDTO,
                                                  Authentication authentication) {
        requireAdmin(authentication);

        Branch branch = branchMapper.toEntity(branchDTO);
        Branch saved = branchService.save(branch);
        BranchDTO savedDto = branchMapper.toDto(saved);

        return ResponseEntity.created(
                        ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(savedDto.id())
                                .toUri())
                .body(savedDto);
    }

    // ── PUT /api/v1/branches/{id} ─────────────────────────────────────────────
    
    @Operation(summary = "Update an existing branch. Admin only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Branch updated"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Forbidden — admin only"),
            @ApiResponse(responseCode = "404", description = "Branch not found")
    })
    @PutMapping("/{id}")
    public BranchDTO updateBranch(@PathVariable Long id,
                                  @Valid @RequestBody BranchDTO branchDTO,
                                  Authentication authentication) {
        requireAdmin(authentication);

        Branch branch = branchService.getBranchById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found: " + id));

        branchMapper.updateEntity(branch, branchDTO);
        Branch updated = branchService.save(branch);
        return branchMapper.toDto(updated);
    }

    // ── DELETE /api/v1/branches/{id} ──────────────────────────────────────────

    @Operation(summary = "Delete a branch. Admin only.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Branch deleted"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Forbidden — admin only"),
            @ApiResponse(responseCode = "404", description = "Branch not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBranch(@PathVariable Long id, Authentication authentication) {
        requireAdmin(authentication);

        Branch branch = branchService.getBranchById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found: " + id));

        branchService.delete(branch.getId());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void requireAdmin(Authentication authentication) {
        User user = resolveUser(authentication);
        if (user.getRole() != User.Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin only");
        }
    }

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