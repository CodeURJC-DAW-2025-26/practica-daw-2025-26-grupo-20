package es.codeurjc.mokaf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/redirect-after-login")
    public String redirectAfterLogin(Authentication authentication) {
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/profileADMIN";
        }
        return "redirect:/profile";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                          @RequestParam String email,
                          @RequestParam String password,
                          HttpServletRequest request,
                          Model model) {
        
        System.out.println("=== REGISTER START ===");
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        
        // Check if email already exists
        if (userService.existsByEmail(email)) {
            System.out.println("Email already exists!");
            model.addAttribute("errorMessage", "Email already registered");
            model.addAttribute("title", "Login - Mokaf");
            return "login";
        }

        try {
            // Create new user
            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPasswordHash(passwordEncoder.encode(password));
            newUser.setRole(User.Role.CUSTOMER);
            
            System.out.println("Saving user...");
            User savedUser = userService.save(newUser);
            System.out.println("User saved with ID: " + savedUser.getId());
            
        } catch (Exception e) {
            System.out.println("ERROR saving user: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error creating user: " + e.getMessage());
            return "login";
        }

        // Auto-login after registration
        try {
            System.out.println("Attempting auto-login...");
            
            UsernamePasswordAuthenticationToken token = 
                new UsernamePasswordAuthenticationToken(email, password);
            
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            request.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
            );
            
            System.out.println("Auto-login successful!");
            System.out.println("=== REGISTER END ===");
            
            return "redirect:/profile";
            
        } catch (Exception e) {
            System.out.println("Auto-login failed: " + e.getMessage());
            System.out.println("=== REGISTER END (fallback to login) ===");
            return "redirect:/login?registered=true";
        }
    }
}