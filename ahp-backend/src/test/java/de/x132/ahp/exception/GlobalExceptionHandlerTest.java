package de.x132.ahp.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");
    }

    @Test
    @DisplayName("Should handle ResourceNotFoundException with 404 status")
    void shouldHandleResourceNotFoundException() {
        // Given
        ResourceNotFoundException exception = new ResourceNotFoundException("Project", "id", 1L);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(exception, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getMessage()).contains("Project not found with id");
        assertThat(response.getBody().getPath()).isEqualTo("/api/test");
    }

    @Test
    @DisplayName("Should handle BadCredentialsException with 401 status")
    void shouldHandleBadCredentialsException() {
        // Given
        BadCredentialsException exception = new BadCredentialsException("Bad credentials");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadCredentialsException(exception, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(401);
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid username or password");
    }

    @Test
    @DisplayName("Should handle UnauthorizedException with 401 status")
    void shouldHandleUnauthorizedException() {
        // Given
        UnauthorizedException exception = new UnauthorizedException("You do not have permission");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUnauthorizedException(exception, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(401);
        assertThat(response.getBody().getMessage()).isEqualTo("You do not have permission");
    }

    @Test
    @DisplayName("Should handle ValidationException with 400 status")
    void shouldHandleValidationException() {
        // Given
        ValidationException exception = new ValidationException("Validation failed");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).isEqualTo("Validation failed");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with field errors")
    void shouldHandleMethodArgumentNotValidException() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError fieldError1 = new FieldError("object", "name", "Name is required");
        FieldError fieldError2 = new FieldError("object", "email", "Email is invalid");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        // When
        ResponseEntity<ValidationErrorResponse> response = 
                exceptionHandler.handleMethodArgumentNotValid(exception, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).isEqualTo("Validation failed");
        assertThat(response.getBody().getErrors()).hasSize(2);
        assertThat(response.getBody().getErrors().get("name")).isEqualTo("Name is required");
        assertThat(response.getBody().getErrors().get("email")).isEqualTo("Email is invalid");
    }

    @Test
    @DisplayName("Should handle InconsistentMatrixException with CR value")
    void shouldHandleInconsistentMatrixException() {
        // Given
        InconsistentMatrixException exception = 
                new InconsistentMatrixException("Matrix is inconsistent", 0.25);

        // When
        ResponseEntity<ErrorResponse> response = 
                exceptionHandler.handleInconsistentMatrixException(exception, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).contains("Matrix is inconsistent");
        // Locale-aware: Accepts both "0.250" (en) and "0,250" (de)
        assertThat(response.getBody().getMessage()).matches(".*CR = 0[.,]250.*");
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException with 400 status")
    void shouldHandleIllegalArgumentException() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        // When
        ResponseEntity<ErrorResponse> response = 
                exceptionHandler.handleIllegalArgumentException(exception, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid argument");
    }

    @Test
    @DisplayName("Should handle generic Exception with 500 status")
    void shouldHandleGenericException() {
        // Given
        Exception exception = new Exception("Unexpected error");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(exception, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getMessage()).contains("unexpected error occurred");
    }

    @Test
    @DisplayName("Should include timestamp in error response")
    void shouldIncludeTimestampInErrorResponse() {
        // Given
        ResourceNotFoundException exception = new ResourceNotFoundException("Not found");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(exception, webRequest);

        // Then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should extract path correctly from WebRequest")
    void shouldExtractPathCorrectly() {
        // Given
        when(webRequest.getDescription(false)).thenReturn("uri=/api/projects/123");
        ResourceNotFoundException exception = new ResourceNotFoundException("Not found");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(exception, webRequest);

        // Then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPath()).isEqualTo("/api/projects/123");
    }
}
