package es.codeurjc.mokaf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public String profile(Authentication authentication, 
                         @RequestParam(value = "updated", required = false) String updated,
                         Model model,
                         HttpServletRequest request) {
        
        System.out.println("\n>>> PROFILE REQUEST <<<");
        
        // Debug session
        HttpSession session = request.getSession(false);
        System.out.println("Session: " + (session != null ? session.getId() : "NULL"));
        if (session != null) {
            System.out.println("Session attributes: ");
            java.util.Enumeration<String> attrs = session.getAttributeNames();
            while (attrs.hasMoreElements()) {
                String attr = attrs.nextElement();
                System.out.println("  - " + attr + ": " + session.getAttribute(attr));
            }
        }
        
        // Debug authentication
        System.out.println("Authentication param: " + authentication);
        System.out.println("SecurityContextHolder: " + SecurityContextHolder.getContext().getAuthentication());
        
        User user = getCurrentUser(authentication);
        
        if (user == null) {
            System.out.println("No user found, redirecting to login");
            System.out.println("<<< END PROFILE >>>\n");
            return "redirect:/login";
        }
        
        System.out.println("User found: " + user.getEmail() + " | Role: " + user.getRole());
        
        if (user.getRole() == User.Role.ADMIN) {
            System.out.println("Admin user, redirecting to profileADMIN");
            System.out.println("<<< END PROFILE >>>\n");
            return "redirect:/profileADMIN";
        }
        
        model.addAttribute("user", user);
        if (updated != null) model.addAttribute("updated", true);
        model.addAttribute("title", "My Profile - Mokaf");
        
        System.out.println("Rendering profile page");
        System.out.println("<<< END PROFILE >>>\n");
        return "profile";
    }

    @GetMapping("/profileADMIN")
    public String profileAdmin(Authentication authentication,
                              @RequestParam(value = "updated", required = false) String updated,
                              Model model,
                              HttpServletRequest request) {
        
        System.out.println("\n>>> PROFILE_ADMIN REQUEST <<<");
        
        HttpSession session = request.getSession(false);
        System.out.println("Session: " + (session != null ? session.getId() : "NULL"));
        System.out.println("Authentication: " + authentication);
        
        User user = getCurrentUser(authentication);
        
        if (user == null) {
            System.out.println("No user found, redirecting to login");
            System.out.println("<<< END PROFILE_ADMIN >>>\n");
            return "redirect:/login";
        }
        
        System.out.println("User: " + user.getEmail() + " | Role: " + user.getRole());
        
        if (user.getRole() != User.Role.ADMIN) {
            System.out.println("Not admin, redirecting to profile");
            System.out.println("<<< END PROFILE_ADMIN >>>\n");
            return "redirect:/profile";
        }
        
        model.addAttribute("user", user);
        if (updated != null) model.addAttribute("updated", true);
        model.addAttribute("title", "Admin Profile - Mokaf");
        
        System.out.println("Rendering profileADMIN page");
        System.out.println("<<< END PROFILE_ADMIN >>>\n");
        return "profileADMIN";
    }

    // ... resto de métodos igual ...

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            // Try SecurityContextHolder as fallback
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