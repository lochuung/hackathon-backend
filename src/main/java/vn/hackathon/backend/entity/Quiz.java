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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "quizzes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {

  @Id @GeneratedValue private UUID id;

  @Column(nullable = false, length = 500)
  private String title;

  @Builder.Default
  @JdbcTypeCode(SqlTypes.ARRAY)
  @Column(name = "skills", columnDefinition = "text[]")
  private List<String> skills = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "created_by")
  private User createdBy;

  private Integer durationMinutes;

  @Builder.Default
  private Double totalPoints = 100.0;

  @Builder.Default
  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  private List<Object> questions = new ArrayList<>();

  @Builder.Default
  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  private Map<String, Object> stats = new HashMap<>();

  @Builder.Default
  private Boolean isPublished = false;

  private Timestamp deletedAt;
  private Timestamp createdAt;
  private Timestamp updatedAt;

  @ManyToOne
  @JoinColumn(name = "class_id")
  private ClassEntity classEntity;
}
