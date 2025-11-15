package vn.hackathon.backend.dto.quiz;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptStartResponse {
  private UUID attemptId;
  private UUID quizId;

  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",
      timezone = "UTC")
  private Instant startTime;

  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",
      timezone = "UTC")
  private Instant expiresAt;

  private Integer durationMinutes;
  private List<QuestionDto> questions;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class QuestionDto {
    private UUID questionId;
    private String question;
    private List<OptionDto> options;
    private List<String> skills;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class OptionDto {
    private int optionIndex = 0;
    private String optionContent;
  }
}
