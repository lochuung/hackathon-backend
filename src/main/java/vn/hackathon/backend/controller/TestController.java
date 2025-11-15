package vn.hackathon.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/test")
public class TestController {

  @PreAuthorize("isAuthenticated()")
  @GetMapping
  public String test() {
    log.info("Test endpoint called");
    return "Test successful";
  }
}
