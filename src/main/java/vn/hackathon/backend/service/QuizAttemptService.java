package vn.hackathon.backend.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import vn.hackathon.backend.dto.quiz.QuizAttemptStartResponse;
import vn.hackathon.backend.dto.quiz.QuizAttemptSubmitResponse;
import vn.hackathon.backend.dto.quiz.QuizPipelineResponse;

public interface QuizAttemptService {
  /**
   * Start a new quiz attempt
   *
   * @param quizId the quiz ID
   * @param userId the user ID
   * @return the attempt start response
   */
  QuizAttemptStartResponse startQuizAttempt(UUID quizId, UUID userId);

  /**
   * Auto-save quiz answers to Redis
   *
   * @param attemptId the attempt ID
   * @param answers the answers map
   */
  void saveAnswers(UUID attemptId, Map<String, Integer> answers);

  /**
   * Submit quiz attempt and calculate score
   *
   * @param attemptId the attempt ID
   * @param answers the answers map
   * @return the submit response
   */
  QuizAttemptSubmitResponse submitQuizAttempt(UUID attemptId, Map<String, Integer> answers);

  /**
   * Get quiz results pipeline for yesterday's completed attempts
   *
   * @return list of quiz pipeline responses
   */
  List<QuizPipelineResponse> getQuizResultsPipeline();
}
