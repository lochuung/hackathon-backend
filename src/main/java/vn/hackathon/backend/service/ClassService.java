package vn.hackathon.backend.service;

import java.util.List;
import java.util.UUID;
import vn.hackathon.backend.dto.ClassDto;

public interface ClassService {

  /**
   * Get all classes with complete information including teacher and student details
   *
   * @return List of all ClassDto objects
   */
  List<ClassDto> getAllClasses();

  /**
   * Get a specific class by ID with complete information
   *
   * @param classId the class ID
   * @return ClassDto with full details
   */
  ClassDto getClassById(UUID classId);
}
