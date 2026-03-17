package es.codeurjc.mokaf.api.dto;

import es.codeurjc.mokaf.model.Allergen;

public class AllergenDTO implements es.codeurjc.mokaf.api.interfaces.AllergenInterface {

    private Long id;
    private String name;

    public AllergenDTO() {
    }

    public AllergenDTO(Allergen allergen) {
        this.id = allergen.getId();
        this.name = allergen.getName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
