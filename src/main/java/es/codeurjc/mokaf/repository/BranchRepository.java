package es.codeurjc.mokaf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.codeurjc.mokaf.model.Branch;

public interface BranchRepository extends JpaRepository<Branch, Long> {
   
    @Query("SELECT b FROM Branch b ORDER BY b.id ASC")
    Optional<Branch> findFirstBranch(); //Necesary for addnig at cart

    @Query("SELECT b FROM Branch b ORDER BY b.id ASC")
    List<Branch> findAllBranchesOrdered();
    
    Optional<Branch> findByName(String name);
}
