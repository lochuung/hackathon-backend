package vn.hackathon.backend.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hackathon.backend.dto.ApiResponse;
import vn.hackathon.backend.dto.quiz.QuizAnswerSaveRequest;
import vn.hackathon.backend.dto.quiz.QuizAttemptStartResponse;
import vn.hackathon.backend.dto.quiz.QuizAttemptSubmitRequest;
import vn.hackathon.backend.dto.quiz.QuizAttemptSubmitResponse;
import vn.hackathon.backend.service.QuizAttemptService;
import vn.hackathon.backend.service.impl.JwtService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class QuizAttemptController {

  private final QuizAttemptService quizAttemptService;
  private final JwtService jwtService;

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/quizzes/{quizId}/attempts")
  public ResponseEntity<ApiResponse<QuizAttemptStartResponse>> startQuizAttempt(
      @PathVariable UUID quizId) {
    UUID currentUserId = jwtService.getUserId();
    QuizAttemptStartResponse response = quizAttemptService.startQuizAttempt(quizId, currentUserId);

    return ResponseEntity.ok(ApiResponse.success("Quiz attempt started successfully", response));
  }

  @PutMapping("/quiz-attempts/{attemptId}/answers")
  public ResponseEntity<ApiResponse<Void>> saveAnswers(
      @PathVariable UUID attemptId, @RequestBody QuizAnswerSaveRequest request) {
    log.info(
        "PUT /api/v1/quiz-attempts/{}/answers - {} answers",
        attemptId,
        request.getAnswers().size());

    quizAttemptService.saveAnswers(attemptId, request.getAnswers());

    return ResponseEntity.ok(ApiResponse.success("Answers saved successfully", null));
  }

  @PostMapping("/quiz-attempts/{attemptId}/submit")
  public ResponseEntity<ApiResponse<QuizAttemptSubmitResponse>> submitQuizAttempt(
      @PathVariable UUID attemptId, @RequestBody QuizAttemptSubmitRequest request) {
    log.info(
        "POST /api/v1/quiz-attempts/{}/submit - {} answers",
        attemptId,
        request.getAnswers().size());

    QuizAttemptSubmitResponse response =
        quizAttemptService.submitQuizAttempt(attemptId, request.getAnswers());

    return ResponseEntity.ok(ApiResponse.success("Quiz attempt submitted successfully", response));
  }
}
