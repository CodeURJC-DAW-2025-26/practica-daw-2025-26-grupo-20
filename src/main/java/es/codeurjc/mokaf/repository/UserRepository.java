package es.codeurjc.mokaf.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.codeurjc.mokaf.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    java.util.List<User> findByRoleIn(java.util.Collection<User.Role> roles);

    java.util.List<User> findByRoleInAndDepartment(java.util.Collection<User.Role> roles, String department);
}