package vn.hackathon.backend.controller;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.hackathon.backend.dto.quiz.QuizPipelineResponse;
import vn.hackathon.backend.service.QuizAttemptService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class QuizPipelineController {
  private final QuizAttemptService quizAttemptService;

  @Value("${app.pipeline.apiKey}")
  private String apiKey;

  @GetMapping("/quiz-results-pipeline")
  public ResponseEntity<List<QuizPipelineResponse>> getQuizResultsPipeline(
      @RequestParam("apiKey") String apiKey) {
    if (!Objects.equals(this.apiKey, apiKey)) {
      return ResponseEntity.status(403).build();
    }
    List<QuizPipelineResponse> results = quizAttemptService.getQuizResultsPipeline();
    return ResponseEntity.ok(results);
  }
}
