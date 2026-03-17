package es.codeurjc.mokaf.api.mapper;

import es.codeurjc.mokaf.api.dto.FaqDTO;
import es.codeurjc.mokaf.model.Faq;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FaqMapper {
    FaqDTO toDTO(Faq faq);
    Faq toEntity(FaqDTO dto);
    void updateEntity(@MappingTarget Faq faq, FaqDTO dto);
}
