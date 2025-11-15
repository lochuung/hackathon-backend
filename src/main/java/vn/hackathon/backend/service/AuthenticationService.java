package vn.hackathon.backend.service;

import vn.hackathon.backend.dto.auth.AuthenticationRequest;
import vn.hackathon.backend.dto.auth.AuthenticationResponse;

public interface AuthenticationService {

  AuthenticationResponse login(AuthenticationRequest loginRequest);
}
