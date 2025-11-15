package vn.hackathon.backend.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.hackathon.backend.entity.Quiz;

public interface QuizRepository extends JpaRepository<Quiz, UUID> {
  /**
   * Find all quizzes for a specific class
   *
   * @param classId the class ID
   * @return List of quizzes for the class
   */
  List<Quiz> findByClassEntity_Id(UUID classId);
}
