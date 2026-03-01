package es.codeurjc.mokaf.service;

import es.codeurjc.mokaf.model.Allergen;
import es.codeurjc.mokaf.repository.AllergenRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AllergenService {

    private final AllergenRepository allergenRepository;

    public AllergenService(AllergenRepository allergenRepository) {
        this.allergenRepository = allergenRepository;
    }

    public List<Allergen> getAllAllergens() {
        return allergenRepository.findAll();
    }

    public Optional<Allergen> findById(Long id) {
        return allergenRepository.findById(id);
    }

    public Set<Allergen> getAllergensByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptySet();
        // JPA: hace 1 query con IN(...)
        List<Allergen> allergens = allergenRepository.findAllById(ids);
        return new HashSet<>(allergens);
    }
}