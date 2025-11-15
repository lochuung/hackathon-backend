package vn.hackathon.backend.domain;

import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.hackathon.backend.entity.ClassEntity;
import vn.hackathon.backend.repository.ClassRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClassDomainService {
  private final ClassRepository classRepository;

  public boolean isParticipantInClass(UUID classId, UUID userId) {
    log.debug("Checking if user: {} is a participant in class: {}", userId, classId);
    ClassEntity classEntity =
        classRepository
            .findById(classId)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        String.format("Class with ID %s not found", classId)));
    if (Objects.equals(classEntity.getCreatedBy().getId(), userId)) {
      return true;
    }
    return classEntity.getStudentIds().stream().anyMatch(id -> id.equals(userId));
  }
}
