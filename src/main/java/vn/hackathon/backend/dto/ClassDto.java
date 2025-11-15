package vn.hackathon.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClassDto {
  private UUID id;
  private String name;
  private UserDto teacher;
  private List<UserDto> students;
  private Boolean isActive;
  private Timestamp createdAt;
  private Timestamp updatedAt;
}
