package vn.hackathon.backend.dto.quiz;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptSubmitResponse {
  private UUID attemptId;
  private Double score;
  private Double totalPoints;
  private Integer correctAnswers;
  private Integer totalQuestions;
  private Map<String, Double> skillsPoint;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
  private Instant endTime;

  private String timeTaken;
}
