package vn.hackathon.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.hackathon.backend.dto.quiz.QuizDto;
import vn.hackathon.backend.entity.Quiz;

@Mapper(
    componentModel = "spring",
    uses = {UserMapper.class})
public interface QuizMapper {

  @Mapping(source = "createdBy", target = "createdBy")
  @Mapping(source = "classEntity.id", target = "classId")
  QuizDto toDto(Quiz quiz);

  @Mapping(source = "createdBy", target = "createdBy")
  @Mapping(target = "classEntity", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  Quiz toEntity(QuizDto quizDto);
}
