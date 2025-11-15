package vn.hackathon.backend.dto.quiz;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerSaveRequest {
  private Map<String, Integer> answers;
}
