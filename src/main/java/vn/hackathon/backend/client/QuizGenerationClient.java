package vn.hackathon.backend.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vn.hackathon.backend.config.FeignConfig;
import vn.hackathon.backend.dto.quiz.QuizGenerationRequest;

@FeignClient(
    name = "quiz-generation-client",
    url = "${app.quiz.generation.url}",
    configuration = FeignConfig.class)
public interface QuizGenerationClient {

  @PostMapping("/createQuiz")
  String createQuiz(@RequestBody QuizGenerationRequest request);
}
