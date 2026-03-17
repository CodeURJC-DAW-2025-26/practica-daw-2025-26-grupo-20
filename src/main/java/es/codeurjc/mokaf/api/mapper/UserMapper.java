package es.codeurjc.mokaf.api.mapper;

import es.codeurjc.mokaf.api.dto.UserDTO;
import es.codeurjc.mokaf.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "imageId", source = "image.id")
    @Mapping(target = "role", expression = "java(user.getRole() != null ? user.getRole().name() : null)")
    UserDTO toDTO(User user);

    @Mapping(target = "role", ignore = true)
    User toEntity(UserDTO dto);

    @Mapping(target = "role", ignore = true)
    void updateEntity(@MappingTarget User user, UserDTO dto);

    @AfterMapping
    default void mapRole(UserDTO dto, @MappingTarget User user) {
        if (dto.role() != null) {
            try {
                user.setRole(User.Role.valueOf(dto.role().toUpperCase().trim()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid role: " + dto.role() + ". Allowed: CUSTOMER, ADMIN, EMPLOYEE");
            }
        }
    }
}
