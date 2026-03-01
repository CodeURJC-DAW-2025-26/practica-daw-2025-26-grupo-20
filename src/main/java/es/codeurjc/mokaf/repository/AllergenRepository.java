package es.codeurjc.mokaf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.codeurjc.mokaf.model.Allergen;

public interface AllergenRepository extends JpaRepository<Allergen, Long> {}
