package de.x132.ahp.exception;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ValidationErrorResponse extends ErrorResponse {
  private Map<String, String> errors;

  public ValidationErrorResponse(
      int status,
      String message,
      String path,
      LocalDateTime timestamp,
      Map<String, String> errors) {
    super(status, message, path, timestamp);
    this.errors = errors;
  }
}
