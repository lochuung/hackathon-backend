package vn.hackathon.backend.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(
    name = "class_enrollments",
    uniqueConstraints = @UniqueConstraint(columnNames = {"class_id", "student_id"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassEnrollment {

  @Id @GeneratedValue private UUID id;

  @ManyToOne
  @JoinColumn(name = "class_id")
  private ClassEntity classEntity;

  @ManyToOne
  @JoinColumn(name = "student_id")
  private User student;

  @Builder.Default
  @Column(length = 50)
  private String enrollmentStatus = "active";

  private Timestamp enrollmentDate;
  private Timestamp completionDate;

  @Builder.Default
  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  private Map<String, Object> stats = new HashMap<>();

  private Timestamp createdAt;
  private Timestamp updatedAt;
}
