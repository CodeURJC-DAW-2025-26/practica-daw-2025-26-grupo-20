package es.codeurjc.mokaf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
        System.out.println("\n>>> REDIRECT-AFTER-LOGIN <<<");
        System.out.println("Authentication: " + authentication);

        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            System.out.println("Redirecting to profileADMIN");
            return "redirect:/profileADMIN";
        }
        System.out.println("Redirecting to profile");
        return "redirect:/profile";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) {

        System.out.println("\n>>> REGISTER <<<");

        // Validation: Email format
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!email.matches(emailRegex)) {
            System.out.println("Invalid email format: " + email);
            model.addAttribute("errorMessage", "Email format is invalid");
            model.addAttribute("title", "Login - Mokaf");
            return "login";
        }

        // Validation: Password complexity (letters and numbers)
        String passwordRegex = "^(?=.*[a-zA-Z])(?=.*\\d).+$";
        if (!password.matches(passwordRegex)) {
            System.out.println("Password complexity validation failed");
            model.addAttribute("errorMessage", "Password must contain both letters and numbers");
            model.addAttribute("title", "Login - Mokaf");
            return "login";
        }

        if (userService.existsByEmail(email)) {
            System.out.println("Email already exists: " + email);
            model.addAttribute("errorMessage", "Email already registered");
            model.addAttribute("title", "Login - Mokaf");
            return "login";
        }

        try {
            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPasswordHash(passwordEncoder.encode(password));
            newUser.setRole(User.Role.CUSTOMER);
            userService.save(newUser);
            System.out.println("User created: " + email);

        } catch (Exception e) {
            System.out.println("Error creating user: " + e.getMessage());
            model.addAttribute("errorMessage", "Error creating user: " + e.getMessage());
            return "login";
        }

        // Auto-login after registration
        try {
            System.out.println("Attempting auto-login...");

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);

            Authentication authentication = authenticationManager.authenticate(token);
            System.out.println("Authentication successful: " + authentication.getName());

            // Create security context
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            // Create session and save context
            HttpSession session = request.getSession(true);
            System.out.println("Session created: " + session.getId());

            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    context);

            // Also set as attribute for compatibility
            session.setAttribute("SPRING_SECURITY_CONTEXT", context);

            System.out.println("Security context saved to session");
            System.out.println("<<< END REGISTER >>>\n");

            return "redirect:/profile";

        } catch (Exception e) {
            System.out.println("Auto-login failed: " + e.getMessage());
            e.printStackTrace();
            System.out.println("<<< END REGISTER (fallback) >>>\n");
            return "redirect:/login?registered=true";
        }
    }
}