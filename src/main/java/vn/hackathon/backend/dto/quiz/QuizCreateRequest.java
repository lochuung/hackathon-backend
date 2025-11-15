package vn.hackathon.backend.dto.quiz;

import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizCreateRequest {
  private String title;

  @Builder.Default private List<String> skills = new ArrayList<>();

  private String documentUrl;
  private Integer durationMinutes;

  @Builder.Default private Double totalPoints = 100.0;

  @Builder.Default private Boolean isPublished = false;

  @NotBlank private String prompt;
}
