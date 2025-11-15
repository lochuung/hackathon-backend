package vn.hackathon.backend.dto.quiz;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizGenerationRequest {

  @JsonProperty("quiz_id")
  private UUID quizId;

  @JsonProperty("prompt")
  private String prompt;

  @JsonProperty("skills")
  private List<String> skills;

  @JsonProperty("document_url")
  private String documentUrl;
}
