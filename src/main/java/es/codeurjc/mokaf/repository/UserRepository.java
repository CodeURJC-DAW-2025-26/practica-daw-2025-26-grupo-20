package es.codeurjc.mokaf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.codeurjc.mokaf.model.User;

public interface UserRepository extends JpaRepository<User, Long> {}
