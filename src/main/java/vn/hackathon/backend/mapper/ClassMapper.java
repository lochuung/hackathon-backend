package vn.hackathon.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.hackathon.backend.dto.ClassDto;
import vn.hackathon.backend.entity.ClassEntity;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ClassMapper {

  @Mapping(source = "createdBy", target = "teacher")
  @Mapping(target = "students", ignore = true)
  ClassDto toDto(ClassEntity classEntity);
}


