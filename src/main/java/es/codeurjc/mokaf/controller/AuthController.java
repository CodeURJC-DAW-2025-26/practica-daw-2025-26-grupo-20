package es.codeurjc.mokaf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SecurityContextRepository securityContextRepository;

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
                          HttpServletResponse response,
                          Model model) {
        
        if (userService.existsByEmail(email)) {
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
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error creating user: " + e.getMessage());
            return "login";
        }

        // Auto-login after registration with persistent session
        try {
            UsernamePasswordAuthenticationToken token = 
                new UsernamePasswordAuthenticationToken(email, password);
            
            Authentication authentication = authenticationManager.authenticate(token);
            
            // Create new security context and set authentication
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            
            // Save security context to session (THIS MAKES IT PERSISTENT)
            securityContextRepository.saveContext(context, request, response);
            
            return "redirect:/profile";
            
        } catch (Exception e) {
            return "redirect:/login?registered=true";
        }
    }
}