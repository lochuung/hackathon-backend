package vn.hackathon.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.hackathon.backend.dto.DocumentDto;
import vn.hackathon.backend.entity.Document;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

  @Mapping(source = "classEntity.id", target = "classId")
  @Mapping(source = "uploadedBy.id", target = "uploadedBy")
  DocumentDto toDto(Document document);

  @Mapping(source = "classId", target = "classEntity.id")
  @Mapping(source = "uploadedBy", target = "uploadedBy.id")
  @Mapping(target = "deletedAt", ignore = true)
  Document toEntity(DocumentDto documentDto);
}

