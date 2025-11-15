package vn.hackathon.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.hackathon.backend.dto.auth.AuthenticationRequest;
import vn.hackathon.backend.dto.auth.AuthenticationResponse;
import vn.hackathon.backend.entity.User;
import vn.hackathon.backend.exception.BadRequestException;
import vn.hackathon.backend.repository.UserRepository;
import vn.hackathon.backend.service.AuthenticationService;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserRepository userRepository;

  /**
   * Authenticates a user and generates access and refresh tokens.
   *
   * @param loginRequest The login request containing the user's email and password.
   * @return An AuthenticationResponse containing access and refresh tokens.
   * @throws BadRequestException if the user is not found, not enabled, or locked.
   */
  public AuthenticationResponse login(AuthenticationRequest loginRequest) {
    User user =
        userRepository
            .findByEmail(loginRequest.getEmail())
            .orElseThrow(
                () ->
                    BadRequestException.message(
                        "Không tìm thấy người dùng: " + loginRequest.getEmail()));

    authenticateUser(user.getEmail(), loginRequest.getPassword());

    String accessToken = jwtService.generateAccessToken(user);

    return new AuthenticationResponse(accessToken);
  }

  /**
   * Authenticates a user by email and password and stores the resulting Authentication in the
   * SecurityContext.
   *
   * <p>On authentication failure this method throws a BadRequestException with a user-facing
   * message. If the account is disabled, it invokes the disabled-account handler before throwing.
   *
   * @param username the user's email (used as the principal)
   * @param password the user's plain-text password
   * @throws BadRequestException if authentication fails for any reason (bad credentials,
   *     locked/expired/disabled account, etc.)
   */
  private void authenticateUser(String username, String password) {
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(username, password));
      SecurityContextHolder.getContext().setAuthentication(authentication);

    } catch (AuthenticationException ex) {
      String message = authenticationExceptionMessage(ex);
      throw BadRequestException.message(message);
    }
  }

  private static String authenticationExceptionMessage(AuthenticationException ex) {
    String message = "Lỗi xác thực không xác định";
    if (ex instanceof BadCredentialsException) {
      message = "Sai tên đăng nhập hoặc mật khẩu";
    } else if (ex instanceof DisabledException) {
      message = "Tài khoản chưa được xác thực, vui lòng kiểm tra email";
    } else if (ex instanceof LockedException) {
      message = "Tài khoản đã bị khóa";
    } else if (ex instanceof AccountExpiredException) {
      message = "Tài khoản đã hết hạn";
    }
    return message;
  }
}
