package vn.hackathon.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hackathon.backend.constants.RoleContants;
import vn.hackathon.backend.entity.User;
import vn.hackathon.backend.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateTestDataService {
  private final UserRepository userRepository;

  @Transactional
  public void createTestingAuthData() {
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
  }
}
