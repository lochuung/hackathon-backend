package vn.hackathon.backend.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "quiz_attempts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttempt {

  @Id @GeneratedValue private UUID id;

  @ManyToOne
  @JoinColumn(name = "quiz_id")
  private Quiz quiz;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column(nullable = false)
  private Timestamp startTime;

  private Timestamp endTime;
  private Timestamp expiresAt;

  @Builder.Default
  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  private Map<String, Object> answers = new HashMap<>();

  @Builder.Default
  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  private Map<String, Object> skillsPoint = new HashMap<>();

  private Double score;

  private Timestamp createdAt;
  private Timestamp updatedAt;
}
