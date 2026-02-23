package es.codeurjc.mokaf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public String profile(Authentication authentication, 
                         @RequestParam(value = "updated", required = false) String updated,
                         Model model) {
        
        User user = getCurrentUser(authentication);
        if (user == null) {
            System.out.println("PROFILE: No authenticated user found, redirecting to login");
            return "redirect:/login";
        }
        
        System.out.println("PROFILE: Authenticated user: " + user.getEmail() + " with role: " + user.getRole());
        
        if (user.getRole() == User.Role.ADMIN) {
            return "redirect:/profileADMIN";
        }
        
        model.addAttribute("user", user);
        if (updated != null) model.addAttribute("updated", true);
        model.addAttribute("title", "My Profile - Mokaf");
        return "profile";
    }

    @GetMapping("/profileADMIN")
    public String profileAdmin(Authentication authentication,
                              @RequestParam(value = "updated", required = false) String updated,
                              Model model) {
        
        User user = getCurrentUser(authentication);
        if (user == null) {
            System.out.println("PROFILE_ADMIN: No authenticated user found, redirecting to login");
            return "redirect:/login";
        }
        
        System.out.println("PROFILE_ADMIN: Authenticated user: " + user.getEmail() + " with role: " + user.getRole());
        
        if (user.getRole() != User.Role.ADMIN) {
            return "redirect:/profile";
        }
        
        model.addAttribute("user", user);
        if (updated != null) model.addAttribute("updated", true);
        model.addAttribute("title", "Admin Profile - Mokaf");
        return "profileADMIN";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam(required = false) String password,
                               Authentication authentication) {
        
        User user = getCurrentUser(authentication);
        if (user == null || user.getRole() != User.Role.CUSTOMER) {
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

        userService.save(user);
        return "redirect:/profile?updated=true";
    }

    @PostMapping("/profileADMIN/update")
    public String updateAdminProfile(@RequestParam String name,
                                    @RequestParam String email,
                                    @RequestParam(required = false) String password,
                                    @RequestParam(required = false) String employeeId,
                                    Authentication authentication) {
        
        User user = getCurrentUser(authentication);
        if (user == null || user.getRole() != User.Role.ADMIN) {
            return "redirect:/login";
        }

        if (!email.equals(user.getEmail()) && userService.existsByEmail(email)) {
            return "redirect:/profileADMIN?error=email_exists";
        }

        user.setName(name);
        user.setEmail(email);
        
        if (password != null && !password.isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(password));
        }
        
        if (employeeId != null && !employeeId.isEmpty()) {
            user.setEmployeeId(employeeId);
        }

        userService.save(user);
        return "redirect:/profileADMIN?updated=true";
    }

    @PostMapping("/profile/delete")
    public String deleteProfile(Authentication authentication, 
                               HttpServletRequest request,
                               HttpServletResponse response) {
        
        User user = getCurrentUser(authentication);
        if (user != null && user.getRole() == User.Role.CUSTOMER) {
            userService.delete(user);
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/login?deleted=true";
    }

    @PostMapping("/profileADMIN/delete")
    public String deleteAdminProfile(Authentication authentication,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        
        User user = getCurrentUser(authentication);
        if (user != null && user.getRole() == User.Role.ADMIN) {
            userService.delete(user);
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/login?deleted=true";
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof User) {
            return (User) principal;
        }
        
        String email = authentication.getName();
        return userService.findByEmail(email).orElse(null);
    }
}