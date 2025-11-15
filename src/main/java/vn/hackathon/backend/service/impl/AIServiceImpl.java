package vn.hackathon.backend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vn.hackathon.backend.client.QuizGenerationClient;
import vn.hackathon.backend.dto.quiz.QuizGenerationRequest;
import vn.hackathon.backend.entity.Quiz;
import vn.hackathon.backend.service.AIService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {
  private final QuizGenerationClient quizGenerationClient;

  @Async
  @Override
  public void callAI(Quiz quiz) {
    try {
      log.info("Calling AI service for quiz ID: {}", quiz.getId());

      QuizGenerationRequest generationRequest =
          QuizGenerationRequest.builder()
              .quizId(quiz.getId())
              .prompt(quiz.getTitle())
              .skills(quiz.getSkills())
              .documentUrl(quiz.getDocumentUrl())
              .build();

      String response = quizGenerationClient.createQuiz(generationRequest);
      log.info("AI service response for quiz ID {}: {}", quiz.getId(), response);
    } catch (Exception e) {
      log.error("Error calling AI service for quiz ID {}: {}", quiz.getId(), e.getMessage(), e);
    }
  }
}
