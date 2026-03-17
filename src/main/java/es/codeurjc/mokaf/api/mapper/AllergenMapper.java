package es.codeurjc.mokaf.api.mapper;

import es.codeurjc.mokaf.api.dto.AllergenDTO;
import es.codeurjc.mokaf.model.Allergen;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AllergenMapper {
    AllergenDTO toDTO(Allergen allergen);
    Allergen toEntity(AllergenDTO dto);
}
