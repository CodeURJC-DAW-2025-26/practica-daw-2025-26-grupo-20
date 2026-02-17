package es.codeurjc.mokaf.mysql.service;

import es.codeurjc.mokaf.mysql.model.User;
import es.codeurjc.mokaf.mysql.model.User.Role;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final List<User> users = new ArrayList<>();

    public UserService() {
        users.add(new User("Admin User", "admin@mokaf.es", "hashedpass", Role.ADMIN));
        users.add(new User("Alice Customer", "alice@test.com", "hashedpass", Role.CUSTOMER));
    }

    public List<User> findAll() {
        return users;
    }

    public Optional<User> findById(Long id) {
        return users.stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId((long) (users.size() + 1));
        }
        users.add(user);
        return user;
    }
}
