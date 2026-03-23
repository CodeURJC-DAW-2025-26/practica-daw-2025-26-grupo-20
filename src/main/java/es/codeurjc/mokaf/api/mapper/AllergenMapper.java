package es.codeurjc.mokaf.api.mapper;

import es.codeurjc.mokaf.api.dto.AllergenDTO;
import es.codeurjc.mokaf.model.Allergen;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AllergenMapper {
    AllergenDTO toDTO(Allergen allergen);
    @Mapping(target = "products", ignore = true)
    Allergen toEntity(AllergenDTO dto);
}
