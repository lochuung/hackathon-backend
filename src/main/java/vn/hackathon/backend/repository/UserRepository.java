package vn.hackathon.backend.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.hackathon.backend.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(
      @NotBlank(message = "Vui lòng nhập email") @Email(message = "Email không hợp lệ")
          String email);
}
