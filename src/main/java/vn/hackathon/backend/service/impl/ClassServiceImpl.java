package vn.hackathon.backend.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hackathon.backend.dto.ClassDto;
import vn.hackathon.backend.dto.UserDto;
import vn.hackathon.backend.entity.ClassEntity;
import vn.hackathon.backend.exception.NotFoundException;
import vn.hackathon.backend.mapper.ClassMapper;
import vn.hackathon.backend.mapper.UserMapper;
import vn.hackathon.backend.repository.ClassRepository;
import vn.hackathon.backend.repository.UserRepository;
import vn.hackathon.backend.service.ClassService;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassServiceImpl implements ClassService {

  private final ClassRepository classRepository;
  private final UserRepository userRepository;
  private final ClassMapper classMapper;
  private final UserMapper userMapper;

  @Override
  @Transactional(readOnly = true)
  public List<ClassDto> getAllClasses() {
    log.debug("Fetching all classes");
    List<ClassEntity> classes = classRepository.findAll();

    return classes.stream().map(this::convertToDto).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public ClassDto getClassById(UUID classId) {
    log.debug("Fetching class with ID: {}", classId);
    ClassEntity classEntity =
        classRepository
            .findById(classId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        String.format("Class with ID %s not found", classId)));

    return convertToDto(classEntity);
  }

  /**
   * Convert ClassEntity to ClassDto with full teacher and student information using MapStruct
   */
  private ClassDto convertToDto(ClassEntity classEntity) {
    // Use MapStruct for basic mapping (teacher and other fields)
    ClassDto classDto = classMapper.toDto(classEntity);

    // Fetch and map students manually (they're stored as UUIDs)
    List<UserDto> studentDtos = List.of();
    if (classEntity.getStudentIds() != null && !classEntity.getStudentIds().isEmpty()) {
      studentDtos =
          classEntity.getStudentIds().stream()
              .map(
                  studentId ->
                      userRepository
                          .findById(studentId)
                          .map(userMapper::toDto)
                          .orElse(null))
              .filter(dto -> dto != null)
              .collect(Collectors.toList());
    }

    classDto.setStudents(studentDtos);
    return classDto;
  }
}

