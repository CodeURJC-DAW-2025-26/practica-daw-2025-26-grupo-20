package es.codeurjc.mokaf.mysql.controller;

import es.codeurjc.mokaf.mysql.model.User;
import es.codeurjc.mokaf.mysql.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("mysqlUserController")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.findById(id).orElse(null);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.save(user);
    }
}
