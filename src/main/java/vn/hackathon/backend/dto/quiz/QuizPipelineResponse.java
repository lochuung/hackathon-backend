package vn.hackathon.backend.dto.quiz;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizPipelineResponse {

  @JsonProperty("student_id")
  private String studentId;

  @JsonProperty("subject_title")
  private String subjectTitle;

  @JsonProperty("total_question")
  private Integer totalQuestion;

  @JsonProperty("test_timestamp")
  private Instant testTimestamp;

  @JsonProperty("total_right_answer")
  private Integer totalRightAnswer;

  @JsonProperty("detail")
  private Map<String, Integer> detail;
}
