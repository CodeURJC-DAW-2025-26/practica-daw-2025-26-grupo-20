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
    private es.codeurjc.mokaf.repository.EmployeeRepository employeeRepository;

    @Autowired
    private es.codeurjc.mokaf.repository.BranchRepository branchRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String showGestionUsuarios(Model model) {
        model.addAttribute("title", "Gestión de Usuarios");
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("allEmployees", employeeRepository.findAll());
        model.addAttribute("branches", branchRepository.findAll());
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
            es.codeurjc.mokaf.model.Employee newEmp = new es.codeurjc.mokaf.model.Employee();
            String generatedId = "EMP-" + java.util.UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            newEmp.setId(generatedId);

            String[] nameParts = name.split(" ", 2);
            newEmp.setFirstName(nameParts[0]);
            newEmp.setLastName(nameParts.length > 1 ? nameParts[1] : "");
            newEmp.setEmail(email);

            newEmp.setPosition(position != null && !position.isEmpty() ? position
                    : (userRole == Role.ADMIN ? "Administrador del Sistema" : "Empleado Nuevo"));
            newEmp.setDepartment(department != null && !department.isEmpty() ? department : "General");
            newEmp.setSalary(salary != null ? salary : new java.math.BigDecimal("2000.00"));
            newEmp.setDescription(description);
            newEmp.setProfileImageUrl("/images/Profile/default.png");

            if (branchId != null) {
                branchRepository.findById(branchId).ifPresent(newEmp::setBranch);
            }

            employeeRepository.save(newEmp);
            newUser.setEmployeeId(generatedId);
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
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) java.math.BigDecimal salary,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String description) {

        Optional<User> optionalUser = userRepository.findById(id);
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
                String targetEmployeeId = employeeId;
                es.codeurjc.mokaf.model.Employee emp;

                if (targetEmployeeId == null || targetEmployeeId.isEmpty()) {
                    // Create new employee if missing
                    emp = new es.codeurjc.mokaf.model.Employee();
                    targetEmployeeId = "EMP-" + java.util.UUID.randomUUID().toString().substring(0, 5).toUpperCase();
                    emp.setId(targetEmployeeId);
                    user.setEmployeeId(targetEmployeeId);
                } else {
                    // Load existing
                    emp = employeeRepository.findById(targetEmployeeId).orElse(new es.codeurjc.mokaf.model.Employee());
                    if (emp.getId() == null) {
                        emp.setId(targetEmployeeId);
                    }
                }

                String[] nameParts = name.split(" ", 2);
                emp.setFirstName(nameParts[0]);
                emp.setLastName(nameParts.length > 1 ? nameParts[1] : "");
                emp.setEmail(email);

                if (position != null)
                    emp.setPosition(position);
                if (department != null)
                    emp.setDepartment(department);
                if (salary != null)
                    emp.setSalary(salary);
                if (description != null)
                    emp.setDescription(description);

                if (branchId != null) {
                    branchRepository.findById(branchId).ifPresent(emp::setBranch);
                } else {
                    emp.setBranch(null);
                }

                if (emp.getProfileImageUrl() == null) {
                    emp.setProfileImageUrl("/images/Profile/default.png");
                }

                employeeRepository.save(emp);
                user.setEmployeeId(targetEmployeeId);
            } else {
                user.setEmployeeId(null);
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
