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
import es.codeurjc.mokaf.service.BranchService;
import es.codeurjc.mokaf.service.UserService;

@Controller
@RequestMapping("/admin/users")
public class GestionUsersController {

    @Autowired
    private UserService userService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String showGestionUsuarios(Model model) {
        model.addAttribute("title", "Gestión de Usuarios");
        model.addAttribute("users", userService.findAll());
        model.addAttribute("allEmployees", userService.getStaff());
        model.addAttribute("branches", branchService.getAllBranches());
        model.addAttribute("availableRoles", User.Role.values());
        model.addAttribute("currentPage", "gestion");
        return "admin/gestion_usuarios";
    }

    @PostMapping("/add")
    public String addUser(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) java.math.BigDecimal salary,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String description) {

        Role userRole = Role.valueOf(role.toUpperCase());
        String encodedPassword = passwordEncoder.encode(password);

        User newUser = new User(name, email, encodedPassword, userRole);

        if (userRole == Role.ADMIN || userRole == Role.EMPLOYEE) {
            String[] nameParts = name.split(" ", 2);
            newUser.setFirstName(nameParts[0]);
            newUser.setLastName(nameParts.length > 1 ? nameParts[1] : "");

            newUser.setPosition(position != null && !position.isEmpty() ? position
                    : (userRole == Role.ADMIN ? "Administrador del Sistema" : "Empleado Nuevo"));
            newUser.setDepartment(department != null && !department.isEmpty() ? department : "General");
            newUser.setSalary(salary != null ? salary : new java.math.BigDecimal("2000.00"));
            newUser.setHireDate(java.time.LocalDateTime.now());
            newUser.setDescription(description);

            if (branchId != null) {
                branchService.getBranchById(branchId).ifPresent(newUser::setBranch);
            }
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
            @RequestParam String role,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) java.math.BigDecimal salary,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String description) {

        Optional<User> optionalUser = userService.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(name);
            user.setEmail(email);
            Role userRole = Role.valueOf(role.toUpperCase());
            user.setRole(userRole);

            if (password != null && !password.trim().isEmpty()) {
                user.setPasswordHash(passwordEncoder.encode(password));
            }

            if (userRole == Role.ADMIN || userRole == Role.EMPLOYEE) {
                String[] nameParts = name.split(" ", 2);
                user.setFirstName(nameParts[0]);
                user.setLastName(nameParts.length > 1 ? nameParts[1] : "");

                if (position != null)
                    user.setPosition(position);
                if (department != null)
                    user.setDepartment(department);
                if (salary != null)
                    user.setSalary(salary);
                if (description != null)
                    user.setDescription(description);

                if (branchId != null) {
                    branchService.getBranchById(branchId).ifPresent(user::setBranch);
                } else {
                    user.setBranch(null);
                }

                if (user.getHireDate() == null) {
                    user.setHireDate(java.time.LocalDateTime.now());
                }
            } else {
                // creater o downgraded to CUSTOMER → clear employee-specific fields
                user.setFirstName(null);
                user.setLastName(null);
                user.setPosition(null);
                user.setDepartment(null);
                user.setSalary(null);
                user.setBranch(null);
                user.setDescription(null);
                user.setHireDate(null);
            }

            userService.save(user);
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        Optional<User> optionalUser = userService.findById(id);
        if (optionalUser.isPresent()) {
            userService.delete(optionalUser.get());
        }
        return "redirect:/admin/users";
    }
}
