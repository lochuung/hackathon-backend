package vn.hackathon.backend.controller;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hackathon.backend.dto.ApiResponse;
import vn.hackathon.backend.dto.ClassDto;
import vn.hackathon.backend.service.ClassService;

@RestController
@RequestMapping("/api/v1/classes")
@RequiredArgsConstructor
@Slf4j
public class ClassController {

  private final ClassService classService;

  /**
   * Get all classes with full information
   *
   * @return List of all classes with teacher and student details
   */
  @GetMapping
  public ResponseEntity<ApiResponse<List<ClassDto>>> getAllClasses() {
    log.info("Request to get all classes");
    List<ClassDto> classes = classService.getAllClasses();
    return ResponseEntity.ok(ApiResponse.success("Classes retrieved successfully", classes));
  }

  /**
   * Get a specific class by ID with full information
   *
   * @param classId the class ID
   * @return Class details with teacher and student information
   */
  @GetMapping("/{classId}")
  public ResponseEntity<ApiResponse<ClassDto>> getClassById(@PathVariable UUID classId) {
    log.info("Request to get class with ID: {}", classId);
    ClassDto classDto = classService.getClassById(classId);
    return ResponseEntity.ok(ApiResponse.success("Class retrieved successfully", classDto));
  }
}
