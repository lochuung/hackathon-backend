package vn.hackathon.backend.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.hackathon.backend.entity.Quiz;

public interface QuizRepository extends JpaRepository<Quiz, UUID> {}
