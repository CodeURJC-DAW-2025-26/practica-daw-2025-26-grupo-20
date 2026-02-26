package es.codeurjc.mokaf.controller;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.mokaf.model.Image;
import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.service.ImageService;
import es.codeurjc.mokaf.service.UserService;

@RestController
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    // Returns images NOT associated with profiles (product images, etc.)
    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        
        Image image = imageService.findById(id).orElse(null);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            int blobLength = (int) image.getImageFile().length();
            byte[] imageBytes = image.getImageFile().getBytes(1, blobLength);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Returns images associated with profiles - only accessible by the owner
    @GetMapping("/profiles/images/{id}")
    public ResponseEntity<byte[]> getImageProfile(Authentication authentication, @PathVariable Long id) {
        // Get current user from authentication or security context
        User user = getCurrentUser(authentication);
        
        if (user == null) {
            System.out.println("Access denied to profile image " + id + ": No authenticated user");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Check if the user owns this image
        if (user.getImage() == null || !user.getImage().getId().equals(id)) {
            System.out.println("Access denied to profile image " + id + ": User " + user.getEmail() + 
                             " does not own this image. User's image ID: " + 
                             (user.getImage() != null ? user.getImage().getId() : "NULL"));
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Image image = imageService.findById(id).orElse(null);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            int blobLength = (int) image.getImageFile().length();
            byte[] imageBytes = image.getImageFile().getBytes(1, blobLength);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
            
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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