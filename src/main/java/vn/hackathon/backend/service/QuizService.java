package vn.hackathon.backend.service;

import java.util.List;
import java.util.UUID;
import vn.hackathon.backend.dto.quiz.QuizCreateRequest;
import vn.hackathon.backend.dto.quiz.QuizDto;

public interface QuizService {

  /**
   * Create a new quiz for a specific class
   *
   * @param quizDto the quiz data
   * @param classId the class ID
   * @param userId the user ID (quiz creator)
   * @return the created QuizDto
   */
  QuizDto createQuiz(QuizCreateRequest quizDto, UUID classId, UUID userId);

  /**
   * Get all quizzes for a specific class
   *
   * @param classId the class ID
   * @return List of QuizDto objects
   */
  List<QuizDto> getQuizzesByClass(UUID classId);

  /**
   * Get a specific quiz by ID
   *
   * @param quizId the quiz ID
   * @return QuizDto with full details
   */
  QuizDto getQuizById(UUID quizId);

  /**
   * Update an existing quiz
   *
   * @param quizId the quiz ID
   * @param quizDto the updated quiz data
   * @param userId the user ID (for authorization)
   * @return the updated QuizDto
   */
  QuizDto updateQuiz(UUID quizId, QuizDto quizDto, UUID userId);
}
