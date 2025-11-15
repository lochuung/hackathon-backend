package vn.hackathon.backend.mapper;

import org.mapstruct.Mapper;
import vn.hackathon.backend.dto.UserDto;
import vn.hackathon.backend.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

  UserDto toDto(User user);

  User toEntity(UserDto userDto);
}
