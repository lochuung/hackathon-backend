package vn.hackathon.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.hackathon.backend.dto.ApiResponse;
import vn.hackathon.backend.dto.auth.AuthenticationRequest;
import vn.hackathon.backend.dto.auth.AuthenticationResponse;
import vn.hackathon.backend.service.AuthenticationService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  private final AuthenticationService authenticationService;

  @PostMapping("/login")
  public ApiResponse<AuthenticationResponse> login(
      @RequestBody @Valid AuthenticationRequest loginRequest) {
    return ApiResponse.success(authenticationService.login(loginRequest));
  }
}
