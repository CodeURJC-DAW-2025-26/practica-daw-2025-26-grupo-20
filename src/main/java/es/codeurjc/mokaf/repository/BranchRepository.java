package es.codeurjc.mokaf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.codeurjc.mokaf.model.Branch;

public interface BranchRepository extends JpaRepository<Branch, Long> {}
