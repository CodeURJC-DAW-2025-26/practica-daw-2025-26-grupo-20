package es.codeurjc.mokaf.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    public User loadUserByEmail(String email) {
        return loadUserByUsername(email);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public java.util.List<User> findAll() {
        return userRepository.findAll();
    }
}