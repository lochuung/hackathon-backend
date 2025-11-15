package vn.hackathon.backend.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.hackathon.backend.entity.QuizAttempt;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {}
