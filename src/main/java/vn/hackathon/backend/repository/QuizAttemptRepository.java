package vn.hackathon.backend.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.hackathon.backend.entity.QuizAttempt;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {

  @Query(
      "SELECT qa FROM QuizAttempt qa WHERE qa.quiz.id = :quizId AND qa.user.id = :userId "
          + "AND qa.endTime IS NULL AND qa.expiresAt > CURRENT_TIMESTAMP")
  List<QuizAttempt> findActiveAttemptsByQuizAndUser(UUID quizId, UUID userId);

  List<QuizAttempt> findAllByEndTimeBetween(Timestamp from, Timestamp to);
}
