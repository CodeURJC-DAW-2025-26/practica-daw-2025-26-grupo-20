package es.codeurjc.mokaf.api.mapper;

import es.codeurjc.mokaf.api.dto.FaqDTO;
import es.codeurjc.mokaf.model.Faq;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FaqMapper {

    FaqDTO toDto(Faq faq);

    @Mapping(target = "id", ignore = true)
    Faq toEntity(FaqDTO faqDTO);
}
