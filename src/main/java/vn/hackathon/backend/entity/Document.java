package vn.hackathon.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "documents")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {

  @Id @GeneratedValue private UUID id;

  @ManyToOne
  @JoinColumn(name = "class_id")
  private ClassEntity classEntity;

  @ManyToOne
  @JoinColumn(name = "uploaded_by")
  private User uploadedBy;

  @Column(nullable = false)
  private String fileName;

  @Column(nullable = false, columnDefinition = "text")
  private String filePath;

  @Column(nullable = false, length = 50)
  private String fileType;

  @Column(columnDefinition = "text")
  private String fileUrl;

  @Column(columnDefinition = "text")
  private String extractedText;

  @Builder.Default private Boolean isActive = true;

  private Timestamp deletedAt;
  private Timestamp createdAt;
  private Timestamp updatedAt;
}
