package vn.hackathon.backend.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hackathon.backend.dto.ApiResponse;
import vn.hackathon.backend.dto.quiz.QuizCreateRequest;
import vn.hackathon.backend.dto.quiz.QuizDto;
import vn.hackathon.backend.service.QuizService;
import vn.hackathon.backend.service.impl.JwtService;

@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
@Slf4j
public class QuizController {

  private final QuizService quizService;
  private final JwtService jwtService;

  /**
   * Create a new quiz for a specific class
   *
   * @param classId the class ID
   * @param quizCreateRequest the quiz data
   * @return the created quiz
   */
  @PostMapping("/class/{classId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<QuizDto>> createQuiz(
      @PathVariable UUID classId, @Valid @RequestBody QuizCreateRequest quizCreateRequest) {
    log.info("Request to create quiz for class: {}", classId);

    // Get userId from JWT token
    JwtAuthenticationToken authentication =
        (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    UUID userId = jwtService.getUserId(authentication);

    QuizDto createdQuiz = quizService.createQuiz(quizCreateRequest, classId, userId);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success("Quiz created successfully", createdQuiz));
  }

  /**
   * Get all quizzes for a specific class
   *
   * @param classId the class ID
   * @return List of quizzes for the class
   */
  @GetMapping("/class/{classId}")
  public ResponseEntity<ApiResponse<List<QuizDto>>> getQuizzesByClass(@PathVariable UUID classId) {
    log.info("Request to get quizzes for class: {}", classId);
    List<QuizDto> quizzes = quizService.getQuizzesByClass(classId);
    return ResponseEntity.ok(ApiResponse.success("Quizzes retrieved successfully", quizzes));
  }

  /**
   * Get a specific quiz by ID
   *
   * @param quizId the quiz ID
   * @return the quiz details
   */
  @GetMapping("/{quizId}")
  public ResponseEntity<ApiResponse<QuizDto>> getQuizById(@PathVariable UUID quizId) {
    log.info("Request to get quiz with ID: {}", quizId);
    QuizDto quiz = quizService.getQuizById(quizId);
    return ResponseEntity.ok(ApiResponse.success("Quiz retrieved successfully", quiz));
  }

  /**
   * Update an existing quiz
   *
   * @param quizId the quiz ID
   * @param quizDto the updated quiz data
   * @return the updated quiz
   */
  @PutMapping("/{quizId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<QuizDto>> updateQuiz(
      @PathVariable UUID quizId, @RequestBody QuizDto quizDto) {
    log.info("Request to update quiz with ID: {}", quizId);

    // Get userId from JWT token
    JwtAuthenticationToken authentication =
        (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    UUID userId = jwtService.getUserId(authentication);

    QuizDto updatedQuiz = quizService.updateQuiz(quizId, quizDto, userId);
    return ResponseEntity.ok(ApiResponse.success("Quiz updated successfully", updatedQuiz));
  }
}
