package vn.hackathon.backend.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hackathon.backend.constants.RoleContants;
import vn.hackathon.backend.entity.ClassEntity;
import vn.hackathon.backend.entity.User;
import vn.hackathon.backend.repository.ClassRepository;
import vn.hackathon.backend.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateTestDataService {
  private final UserRepository userRepository;
  private final ClassRepository classRepository;

  @Transactional
  public void createTestingAuthData() {
    if (userRepository.count() > 0) {
      log.info("Testing auth data already exists, skipping creation.");
      return;
    }
    User teacher =
        User.builder()
            .fullName("Nguyen Huu Loc")
            .password("$2a$12$rsUMHqZGKjt88kU/odqA0eRYb0AofZ7U1SLq78uTs5/LNpfH2Brk6")
            .email("locn562836@gmail.com")
            .role(RoleContants.TEACHER)
            .build();

    User student =
        User.builder()
            .fullName("Toan Cho Dien")
            .password("$2a$12$y7C/7rd9SuijqfGT2GXw2uQQITG6TU5roffomE1aSwRqur5M/maFS")
            .email("thanhnoobs@gmail.com")
            .role(RoleContants.STUDENT)
            .build();

    userRepository.save(teacher);
    userRepository.save(student);

    log.info("Created testing auth data successfully.");
    createTestingClassData(teacher, student);
  }

  @Transactional
  protected void createTestingClassData(User teacher, User student) {
    if (classRepository.count() > 0) {
      log.info("Testing class data already exists, skipping creation.");
      return;
    }

    Timestamp now = Timestamp.from(Instant.now());

    // VD mock 1 lớp có 1 student
    ClassEntity javaClass =
        ClassEntity.builder()
            .name("Lop Java Can Ban")
            .createdBy(teacher)
            .studentIds(List.of(student.getId()))
            .metadata(
                Map.of(
                    "description", "Lop hoc Java cho nguoi moi bat dau",
                    "level", "BEGINNER",
                    "room", "A101",
                    "maxStudent", 30,
                    "schedule", List.of("Mon-18:00", "Wed-18:00")))
            .isActive(true)
            .createdAt(now)
            .updatedAt(now)
            .build();

    // Mock thêm 1 lớp khác
    ClassEntity springBootClass =
        ClassEntity.builder()
            .name("Spring Boot Nang Cao")
            .createdBy(teacher)
            .studentIds(List.of(student.getId()))
            .metadata(
                Map.of(
                    "description", "Khoa hoc Spring Boot nang cao",
                    "level", "ADVANCED",
                    "room", "B202",
                    "semester", "2025-1",
                    "tags", List.of("spring", "boot", "rest-api")))
            .isActive(true)
            .createdAt(now)
            .updatedAt(now)
            .build();

    classRepository.save(javaClass);
    classRepository.save(springBootClass);

    log.info("Created testing class data successfully.");
  }
}
