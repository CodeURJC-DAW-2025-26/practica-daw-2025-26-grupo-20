package es.codeurjc.mokaf.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.model.User.Role;
import es.codeurjc.mokaf.repository.UserRepository;
import es.codeurjc.mokaf.service.UserService;

@Controller
@RequestMapping("/admin/users")
public class GestionUsersController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String showGestionUsuarios(Model model) {
        model.addAttribute("title", "Gestión de Usuarios");
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("currentPage", "gestion");
        return "admin/gestion_usuarios";
    }

    @PostMapping("/add")
    public String addUser(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            @RequestParam(required = false) String employeeId) {

        Role userRole = Role.valueOf(role.toUpperCase());
        String encodedPassword = passwordEncoder.encode(password);

        User newUser = new User(name, email, encodedPassword, userRole);
        if (employeeId != null && !employeeId.trim().isEmpty()) {
            newUser.setEmployeeId(employeeId.trim());
        }

        userService.save(newUser);
        return "redirect:/admin/users";
    }

    @PostMapping("/edit")
    public String editUser(
            @RequestParam Long id,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam(required = false) String password,
            @RequestParam String role) {

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(name);
            user.setEmail(email);
            user.setRole(Role.valueOf(role.toUpperCase()));

            if (password != null && !password.trim().isEmpty()) {
                user.setPasswordHash(passwordEncoder.encode(password));
            }

            userService.save(user);
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            userService.delete(optionalUser.get());
        }
        return "redirect:/admin/users";
    }
}
