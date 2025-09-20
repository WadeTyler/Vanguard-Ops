package tech.vanguardops.vanguardops.auth;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import tech.vanguardops.vanguardops.auth.dto.UserDTO;

/**
 * Mapper for converting User entities to UserDTOs.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(source = "authority", target = "authority", qualifiedByName = "mapAuthority")
    UserDTO toDTO(User user);

    @Named("mapAuthority")
    default String mapAuthority(Authority authority) {
        return authority != null ? authority.getAuthority() : null;
    }
}