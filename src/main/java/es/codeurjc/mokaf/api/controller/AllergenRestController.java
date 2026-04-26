package es.codeurjc.mokaf.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.mokaf.api.dto.AllergenDTO;
import es.codeurjc.mokaf.model.Allergen;
import es.codeurjc.mokaf.service.AllergenService;

@RestController
@RequestMapping("/api/v1/allergens")
public class AllergenRestController {

    @Autowired
    private AllergenService allergenService;

    @GetMapping
    public List<AllergenDTO> getAllAllergens() {
        List<Allergen> allergens = allergenService.getAllAllergens();
        return allergens.stream()
                .map(a -> new AllergenDTO(a.getId(), a.getName()))
                .collect(Collectors.toList());
    }
}
