package es.codeurjc.mokaf.api.mapper;

import es.codeurjc.mokaf.api.dto.BranchDTO;
import es.codeurjc.mokaf.model.Branch;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BranchMapper {
    BranchDTO toDto(Branch branch);
}
