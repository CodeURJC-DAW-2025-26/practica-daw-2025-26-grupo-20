package es.codeurjc.mokaf.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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

    // ============================================
    // PERFIL DE USUARIO NORMAL (CUSTOMER)
    // ============================================

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

        if (user.getRole() == User.Role.ADMIN) {
            return "redirect:/profileADMIN";
        }

        // Recargar desde BBDD para tener datos frescos
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

        // Recargar usuario desde BBDD
        user = userService.findByEmail(user.getEmail()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        // Guardar ID antiguo de imagen para borrar después
        Long oldImageId = user.getImage() != null ? user.getImage().getId() : null;

        // Update basic info
        user.setName(name);
        user.setEmail(email);

        if (password != null && !password.isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(password));
        }

        // Procesar nueva imagen si se proporcionó
        if (image != null && !image.isEmpty()) {
            try {
                System.out.println("[USER] Processing image: " + image.getOriginalFilename());
                
                // Crear nueva imagen
                Image newImage = imageService.createImage(image);
                
                if (newImage != null) {
                    // Asignar nueva imagen al usuario
                    user.setImage(newImage);
                    System.out.println("[USER] New image created with ID: " + newImage.getId());
                }
            } catch (IOException e) {
                System.out.println("[USER] Error processing image: " + e.getMessage());
                return "redirect:/profile?error=image_upload_failed";
            }
        }

        // Guardar usuario (esto guarda también la imagen por cascade)
        User savedUser = userService.save(user);
        System.out.println("[USER] User saved with image ID: " + 
            (savedUser.getImage() != null ? savedUser.getImage().getId() : "null"));

        // Borrar imagen antigua si se subió nueva
        if (image != null && !image.isEmpty() && oldImageId != null) {
            try {
                imageService.deleteImage(oldImageId);
                System.out.println("[USER] Old image deleted: " + oldImageId);
            } catch (Exception e) {
                System.out.println("[USER] Could not delete old image: " + e.getMessage());
            }
        }

        // IMPORTANTE: Recargar usuario completo desde BBDD
        User refreshedUser = userService.findById(savedUser.getId()).orElse(savedUser);
        
        // Forzar carga de la imagen si existe
        if (refreshedUser.getImage() != null) {
            refreshedUser.getImage().getId(); // Tocar para forzar lazy load
        }

        // Actualizar autenticación con usuario refrescado
        updateAuthentication(refreshedUser, request, response);

        return "redirect:/profile?updated=true";
    }

    @PostMapping("/profile/delete")
    public String deleteProfile(Authentication authentication,
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

        try {
            if (user.getImage() != null) {
                imageService.deleteImage(user.getImage().getId());
            }
            userService.delete(user);
        } catch (Exception e) {
            System.out.println("[USER] Error deleting: " + e.getMessage());
            return "redirect:/profile?error=delete_failed";
        }

        return "redirect:/login?deleted=true";
    }

    // ============================================
    // PERFIL DE ADMINISTRADOR
    // ============================================

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

        user = userService.findByEmail(user.getEmail()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Long oldImageId = user.getImage() != null ? user.getImage().getId() : null;

        user.setName(name);
        user.setEmail(email);

        if (password != null && !password.isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(password));
        }

        if (image != null && !image.isEmpty()) {
            try {
                System.out.println("[ADMIN] Processing image: " + image.getOriginalFilename());
                
                Image newImage = imageService.createImage(image);
                
                if (newImage != null) {
                    user.setImage(newImage);
                    System.out.println("[ADMIN] New image created with ID: " + newImage.getId());
                }
            } catch (IOException e) {
                System.out.println("[ADMIN] Error: " + e.getMessage());
                return "redirect:/profileADMIN?error=image_upload_failed";
            }
        }

        User savedUser = userService.save(user);
        System.out.println("[ADMIN] User saved with image ID: " + 
            (savedUser.getImage() != null ? savedUser.getImage().getId() : "null"));

        if (image != null && !image.isEmpty() && oldImageId != null) {
            try {
                imageService.deleteImage(oldImageId);
                System.out.println("[ADMIN] Old image deleted: " + oldImageId);
            } catch (Exception e) {
                System.out.println("[ADMIN] Could not delete old image: " + e.getMessage());
            }
        }

        User refreshedUser = userService.findById(savedUser.getId()).orElse(savedUser);
        
        if (refreshedUser.getImage() != null) {
            refreshedUser.getImage().getId();
        }

        updateAuthentication(refreshedUser, request, response);

        return "redirect:/profileADMIN?updated=true";
    }

    @PostMapping("/profileADMIN/delete")
    public String deleteAdminProfile(Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response) {

        User authUser = getCurrentUser(authentication);

        if (authUser == null || authUser.getRole() != User.Role.ADMIN) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(authUser.getEmail()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            if (user.getImage() != null) {
                imageService.deleteImage(user.getImage().getId());
            }
            userService.delete(user);
        } catch (Exception e) {
            System.out.println("[ADMIN] Error deleting: " + e.getMessage());
            return "redirect:/profileADMIN?error=delete_failed";
        }

        return "redirect:/login?deleted=true";
    }

    // ============================================
    // MÉTODOS PRIVADOS
    // ============================================

    private void updateAuthentication(User updatedUser, HttpServletRequest request, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                updatedUser,
                null,
                updatedUser.getAuthorities()
        );

        newAuth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        request.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );
        
        System.out.println("[AUTH] Updated: " + updatedUser.getEmail() + 
                          " | Image: " + (updatedUser.getImage() != null ? updatedUser.getImage().getId() : "null"));
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