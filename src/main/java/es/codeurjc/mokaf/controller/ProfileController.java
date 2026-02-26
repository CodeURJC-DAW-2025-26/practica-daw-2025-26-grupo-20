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
            @RequestParam(value = "error", required = false) String error,
            Model model,
            HttpServletRequest request) {

        User user = getCurrentUser(authentication);
        if (user == null) {
            return "redirect:/login";
        }

        // Si es admin, redirigir a su perfil
        if (user.getRole() == User.Role.ADMIN) {
            return "redirect:/profileADMIN";
        }

        // Recargar usuario desde BBDD para tener datos frescos (incluyendo imagen)
        user = userService.findByEmail(user.getEmail()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        if (updated != null)
            model.addAttribute("updated", true);
        if (error != null)
            model.addAttribute("error", error);
        model.addAttribute("title", "My Profile - Mokaf");

        return "profile";
    }

    @GetMapping("/profileADMIN")
    public String profileAdmin(Authentication authentication,
            @RequestParam(value = "updated", required = false) String updated,
            @RequestParam(value = "error", required = false) String error,
            Model model,
            HttpServletRequest request) {

        User user = getCurrentUser(authentication);
        if (user == null) {
            return "redirect:/login";
        }

        if (user.getRole() != User.Role.ADMIN) {
            return "redirect:/profile";
        }

        // Recargar usuario desde BBDD para tener datos frescos
        user = userService.findByEmail(user.getEmail()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        if (updated != null)
            model.addAttribute("updated", true);
        if (error != null)
            model.addAttribute("error", error);
        model.addAttribute("title", "Admin Profile - Mokaf");

        return "profileADMIN";
    }

    @PostMapping("/profile/update")
    public String updateProfile(Authentication authentication,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) MultipartFile image,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        User user = getCurrentUser(authentication);
        if (user == null)
            return "redirect:/login";

        // RELOAD user from database to ensure we have the latest data
        user = userService.findByEmail(user.getEmail()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        // Update basic info
        user.setName(name);
        user.setEmail(email);

        if (password != null && !password.isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(password));
        }

        // Update image if provided
        if (image != null && !image.isEmpty()) {
            try {
                System.out.println("Uploading user image: " + image.getOriginalFilename());
                Image newImage = imageService.updateImage(
                        user.getImage() != null ? user.getImage().getId() : null,
                        image
                );
                if (newImage != null) {
                    user.setImage(newImage);
                    System.out.println("User image saved with ID: " + newImage.getId());
                }
            } catch (IOException e) {
                System.out.println("Error uploading image: " + e.getMessage());
                e.printStackTrace();
                return "redirect:/profile?error=image_upload_failed";
            }
        }

        User savedUser = userService.save(user);
        System.out.println("User saved. Image ID: " + (savedUser.getImage() != null ? savedUser.getImage().getId() : "NULL"));

        // IMPORTANT: Reload user from DB to ensure we have the complete object with image
        User refreshedUser = userService.findById(savedUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found after save"));
        
        System.out.println("Refreshed user image ID: " + (refreshedUser.getImage() != null ? refreshedUser.getImage().getId() : "NULL"));

        // Update authentication with refreshed user
        updateAuthentication(refreshedUser, request, response);

        return "redirect:/profile?updated=true";
    }

    @PostMapping("/profileADMIN/update")
    public String updateProfileAdmin(Authentication authentication,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) MultipartFile image,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        User user = getCurrentUser(authentication);
        if (user == null)
            return "redirect:/login";
        if (user.getRole() != User.Role.ADMIN)
            return "redirect:/profile";

        // RELOAD user from database
        user = userService.findByEmail(user.getEmail()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        user.setName(name);
        user.setEmail(email);

        if (password != null && !password.isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(password));
        }

        // Update image if provided
        if (image != null && !image.isEmpty()) {
            try {
                System.out.println("Uploading admin image: " + image.getOriginalFilename());
                Image newImage = imageService.updateImage(
                        user.getImage() != null ? user.getImage().getId() : null,
                        image
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
        
        // IMPORTANT: Reload user from DB to ensure we have the complete object with image
        User refreshedUser = userService.findById(savedUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found after save"));
        
        System.out.println("Refreshed admin user image ID: " + (refreshedUser.getImage() != null ? refreshedUser.getImage().getId() : "NULL"));

        // Update authentication with refreshed user
        updateAuthentication(refreshedUser, request, response);

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
        request.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );
        
        System.out.println("Authentication updated for user: " + updatedUser.getEmail() + 
                          " with image ID: " + (updatedUser.getImage() != null ? updatedUser.getImage().getId() : "NULL"));
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

        // If principal is String (username), load from database
        if (principal instanceof String) {
            String email = (String) principal;
            return userService.findByEmail(email).orElse(null);
        }

        return null;
    }
}