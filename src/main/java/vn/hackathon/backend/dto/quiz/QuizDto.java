package vn.hackathon.backend.dto.quiz;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hackathon.backend.dto.UserDto;

@Data
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuizDto {
  private UUID id;
  private String title;

  @Builder.Default private List<String> skills = new ArrayList<>();

  private String documentUrl;

  private UserDto createdBy;
  private Integer durationMinutes;

  @Builder.Default private Double totalPoints = 100.0;

  @Builder.Default private List<Object> questions = new ArrayList<>();

  @Builder.Default private Map<String, Object> stats = new HashMap<>();

  @Builder.Default private Boolean isPublished = false;

  private UUID classId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
