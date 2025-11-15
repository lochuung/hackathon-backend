package vn.hackathon.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import vn.hackathon.backend.service.CreateTestDataService;

@SpringBootApplication
public class UBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(UBackendApplication.class, args);
  }

  @Bean
  public CommandLineRunner runner(CreateTestDataService testDataService) {
    return args -> {
      testDataService.createTestingAuthData();
    };
  }
}
