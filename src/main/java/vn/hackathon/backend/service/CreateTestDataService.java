package vn.hackathon.backend.service;

import java.time.LocalDateTime;
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

    User student1 =
        User.builder()
            .studentId("22110179")
            .fullName("Nguyen Van A")
            .password("$2a$12$y7C/7rd9SuijqfGT2GXw2uQQITG6TU5roffomE1aSwRqur5M/maFS")
            .email("thanhnoobs@gmail.com")
            .role(RoleContants.STUDENT)
            .build();

    User student2 =
        User.builder()
            .studentId("22110111")
            .fullName("Student Test")
            .password("$2a$12$y7C/7rd9SuijqfGT2GXw2uQQITG6TU5roffomE1aSwRqur5M/maFS") // password:
            // password123
            .email("abc@gmail.com")
            .role(RoleContants.STUDENT)
            .build();

    userRepository.save(teacher);
    userRepository.save(student1);
    userRepository.save(student2);

    log.info("Created testing auth data successfully.");
    createTestingClassData(teacher, student1, student2);
  }

  @Transactional
  protected void createTestingClassData(User teacher, User student1, User student2) {
    if (classRepository.count() > 0) {
      log.info("Testing class data already exists, skipping creation.");
      return;
    }

    LocalDateTime now = LocalDateTime.now();

    // VD mock 1 lớp có 1 student
    ClassEntity javaClass =
        ClassEntity.builder()
            .name("Lập trình di động")
            .createdBy(teacher)
            .studentIds(List.of(student1.getId(), student2.getId()))
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
            .name("Hệ quản trị cơ sở dữ liệu")
            .createdBy(teacher)
            .studentIds(List.of(student1.getId()))
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
