package vn.hackathon.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hackathon.backend.constants.MockDataConstants;

@RestController
@RequestMapping("/api/v1/academic-summaries")
public class AcademicSummaryController {
  @GetMapping
  public String getAcademicSummaries() {
    return MockDataConstants.MOCK_ACADEMIC_TRANSPORT;
  }
}
