package vn.hackathon.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentDto {
  private UUID id;
  private UUID classId;
  private UUID uploadedBy;
  private String fileName;
  private String filePath;
  private String fileType;
  private String fileUrl;
  private String extractedText;
  private Boolean isActive;
  private Timestamp createdAt;
  private Timestamp updatedAt;
}
