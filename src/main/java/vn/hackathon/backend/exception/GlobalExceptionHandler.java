package vn.hackathon.backend.exception;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import java.nio.file.AccessDeniedException;
import java.util.List;
import lombok.NonNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import vn.hackathon.backend.dto.ApiResponse;

@ControllerAdvice
@ResponseBody
@Order(HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
  @ExceptionHandler({BadRequestException.class})
  public ResponseEntity<Object> badRequest(BadRequestException e) {
    logger.error(e.getMessage(), e.getCause());
    return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException e,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status,
      @NonNull WebRequest request) {
    logger.error(e.getMessage(), e.getCause());

    List<String> errors =
        e.getBindingResult().getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .toList();

    return ResponseEntity.badRequest().body(ApiResponse.error(String.join(", ", errors)));
  }

  @ExceptionHandler(SecurityException.class)
  public ResponseEntity<ApiResponse<Object>> handleSecurityException(SecurityException ex) {
    logger.error(ex.getMessage(), ex.getCause());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponse.error("Authentication required"));
  }

  @ExceptionHandler({AccessDeniedException.class})
  public ResponseEntity<Object> accessDenied(AccessDeniedException e) {
    logger.error(e.getMessage(), e.getCause());

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(e.getMessage()));
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<Object> ex(Exception e) {
    logger.error(e.getMessage(), e.getCause());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error(e.getMessage()));
  }
}
