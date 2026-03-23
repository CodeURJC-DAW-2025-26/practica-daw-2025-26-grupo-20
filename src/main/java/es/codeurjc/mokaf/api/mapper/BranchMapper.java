package es.codeurjc.mokaf.api.mapper;

import es.codeurjc.mokaf.api.dto.BranchDTO;
import es.codeurjc.mokaf.model.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BranchMapper {

    BranchDTO toDto(Branch branch);

    @Mapping(target = "id", ignore = true)
    Branch toEntity(BranchDTO branchDTO);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Branch branch, BranchDTO branchDTO);
}