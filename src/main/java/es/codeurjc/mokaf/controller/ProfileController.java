package es.codeurjc.mokaf.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.mokaf.model.Image;
import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.service.ImageService;
import es.codeurjc.mokaf.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ImageService imageService;

    @GetMapping("/profile")
    public String profile(Authentication authentication,
            @RequestParam(value = "updated", required = false) String updated,
            Model model,
            HttpServletRequest request) {

        User user = getCurrentUser(authentication);

        if (user == null) {
            return "redirect:/login";
        }

        if (user.getRole() == User.Role.ADMIN) {
            return "redirect:/profileADMIN";
        }

        model.addAttribute("user", user);
        if (updated != null) {
            model.addAttribute("updated", true);
        }
        model.addAttribute("title", "My Profile - Mokaf");

        return "profile";
    }

    @GetMapping("/profileADMIN")
    public String profileAdmin(Authentication authentication,
            @RequestParam(value = "updated", required = false) String updated,
            Model model,
            HttpServletRequest request) {

        User user = getCurrentUser(authentication);

        if (user == null) {
            return "redirect:/login";
        }

        if (user.getRole() != User.Role.ADMIN) {
            return "redirect:/profile";
        }

        model.addAttribute("user", user);
        if (updated != null) {
            model.addAttribute("updated", true);
        }
        model.addAttribute("title", "Admin Profile - Mokaf");

        return "profileADMIN";
    }
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String name,
            @RequestParam String email,
            @RequestParam(required = false) String password,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response) {

        User authUser = getCurrentUser(authentication);
        if (authUser == null || authUser.getRole() != User.Role.CUSTOMER) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(authUser.getEmail()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        if (!email.equals(user.getEmail()) && userService.existsByEmail(email)) {
            return "redirect:/profile?error=email_exists";
        }

        user.setName(name);
        user.setEmail(email);

        if (password != null && !password.isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(password));
        }

        Long newImageId = null;

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                Image newImage = imageService.updateImage(
                        user.getImage() != null ? user.getImage().getId() : null,
                        imageFile
                );
                if (newImage != null) {
                    user.setImage(newImage);
                    newImageId = newImage.getId();
                    System.out.println(">>> Nueva imagen asignada con ID: " + newImageId);
                }
            } catch (IOException e) {
                return "redirect:/profile?error=image_upload_failed";
            }
        }

        // Guardar usuario
        User savedUser = userService.save(user);
        System.out.println(">>> Usuario guardado. ID: " + savedUser.getId());

       
        final String emailToReload = email;

        java.util.Optional<User> userOpt = userService.findByEmail(emailToReload);
        User refreshedUser;

        if (userOpt.isPresent()) {
            refreshedUser = userOpt.get();
            System.out.println(">>> Usuario recargado correctamente");
        } else {
            refreshedUser = savedUser; // Fallback al objeto guardado
            System.out.println(">>> WARNING: No se pudo recargar, usando savedUser");
        }

        // Inicializar imagen si existe
        if (refreshedUser.getImage() != null) {
            refreshedUser.getImage().getId(); // Touch para inicializar
            System.out.println(">>> Imagen inicializada. ID: " + refreshedUser.getImage().getId());
        }

        // Actualizar autenticación
        updateAuthentication(refreshedUser, request, response);

        return "redirect:/profile?updated=true";
    }

    @PostMapping("/profileADMIN/update")
    public String updateAdminProfile(@RequestParam String name,
            @RequestParam String email,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String employeeId,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response) {

        // Get user from authentication
        User authUser = getCurrentUser(authentication);
        if (authUser == null || authUser.getRole() != User.Role.ADMIN) {
            return "redirect:/login";
        }

        // RELOAD user from database to ensure it's attached to current session
        User user = userService.findByEmail(authUser.getEmail()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        if (!email.equals(user.getEmail()) && userService.existsByEmail(email)) {
            return "redirect:/profileADMIN?error=email_exists";
        }

        // Update basic info
        user.setName(name);
        user.setEmail(email);

        if (password != null && !password.isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(password));
        }

        if (employeeId != null && !employeeId.isEmpty()) {
            user.setEmployeeId(employeeId);
        }

        // Update image if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                System.out.println("Uploading admin image: " + imageFile.getOriginalFilename());
                Image newImage = imageService.updateImage(
                        user.getImage() != null ? user.getImage().getId() : null,
                        imageFile
                );
                if (newImage != null) {
                    user.setImage(newImage);
                    System.out.println("Admin image saved with ID: " + newImage.getId());
                }
            } catch (IOException e) {
                System.out.println("Error uploading admin image: " + e.getMessage());
                e.printStackTrace();
                return "redirect:/profileADMIN?error=image_upload_failed";
            }
        }

        User savedUser = userService.save(user);
        System.out.println("Admin user saved. Image ID: " + (savedUser.getImage() != null ? savedUser.getImage().getId() : "NULL"));

        // IMPORTANTE: Actualizar el Authentication con los nuevos datos
        updateAuthentication(savedUser, request, response);

        return "redirect:/profileADMIN?updated=true";
    }

    /**
     * Update the SecurityContext with the updated user
     */
    private void updateAuthentication(User updatedUser, HttpServletRequest request, HttpServletResponse response) {
        // Create new authentication token with updated user
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                updatedUser,
                null,
                updatedUser.getAuthorities()
        );

        newAuth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Update SecurityContext
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        // Save to session
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());
    }

    @PostMapping("/profile/delete")
    public String deleteProfile(Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response) {

        User authUser = getCurrentUser(authentication);

        if (authUser == null || authUser.getRole() != User.Role.CUSTOMER) {
            return "redirect:/login";
        }

        // RELOAD user from database
        User user = userService.findByEmail(authUser.getEmail()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            // Delete user's image first if exists
            if (user.getImage() != null) {
                imageService.deleteImage(user.getImage().getId());
            }

            userService.delete(user);
            new SecurityContextLogoutHandler().logout(request, response, authentication);

        } catch (Exception e) {
            System.out.println("Error deleting user: " + e.getMessage());
            return "redirect:/profile?error=delete_failed";
        }

        return "redirect:/login?deleted=true";
    }

    @PostMapping("/profileADMIN/delete")
    public String deleteAdminProfile(Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response) {

        User authUser = getCurrentUser(authentication);

        if (authUser == null || authUser.getRole() != User.Role.ADMIN) {
            return "redirect:/login";
        }

        // RELOAD user from database
        User user = userService.findByEmail(authUser.getEmail()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            // Delete user's image first if exists
            if (user.getImage() != null) {
                imageService.deleteImage(user.getImage().getId());
            }

            userService.delete(user);
            new SecurityContextLogoutHandler().logout(request, response, authentication);

        } catch (Exception e) {
            System.out.println("Error deleting admin user: " + e.getMessage());
            return "redirect:/profileADMIN?error=delete_failed";
        }

        return "redirect:/login?deleted=true";
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            return (User) principal;
        }

        if (principal instanceof String) {
            String email = (String) principal;
            return userService.findByEmail(email).orElse(null);
        }

        return null;
    }
}
