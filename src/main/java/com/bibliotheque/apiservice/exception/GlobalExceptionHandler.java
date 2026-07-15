package com.bibliotheque.apiservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private ApiError buildError(HttpStatus status, String message) {
    return new ApiError(status.value(), status.getReasonPhrase(), message);
  }

  // 204 - No content
  @ExceptionHandler({ NoContentException.class })
  public ResponseEntity<ApiError> handleNoContentException(NoContentException ex) {
    ApiError apiError = buildError(HttpStatus.NO_CONTENT, ex.getMessage());
    return new ResponseEntity<>(apiError, HttpStatus.NO_CONTENT);
  }

  // 400 - Bad request
  @ExceptionHandler({ BadRequestException.class })
  public ResponseEntity<ApiError> handleBadRequestException(BadRequestException ex) {
    ApiError apiError = buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  // 401 - Unauthorized
  @ExceptionHandler({ UnauthorizedException.class })
  public ResponseEntity<ApiError> handleUnauthorizedException(UnauthorizedException ex) {
    ApiError apiError = buildError(HttpStatus.UNAUTHORIZED, ex.getMessage());
    return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
  }

  // 403 - Forbidden
  @ExceptionHandler({ ForbiddenException.class })
  public ResponseEntity<ApiError> handleForbiddenException(ForbiddenException ex) {
    ApiError apiError = buildError(HttpStatus.FORBIDDEN, ex.getMessage());
    return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
  }

  // 404 - Not found
  @ExceptionHandler({ ResourceNotFoundException.class })
  public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex) {
    ApiError apiError = buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
  }

  // 409 - Conflict
  @ExceptionHandler({ DataConflictException.class })
  public ResponseEntity<ApiError> handleConflictException(DataConflictException ex) {
    ApiError apiError = buildError(HttpStatus.CONFLICT, ex.getMessage());
    return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
  }

  // 500 - Internal server error
  @ExceptionHandler({ InternalServerException.class })
  public ResponseEntity<ApiError> handleInternalServerException(InternalServerException ex) {
    ApiError apiError = buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // 503 - Service unavailable
  @ExceptionHandler({ ServiceUnavailableException.class })
  public ResponseEntity<ApiError> handleServiceUnavailableException(ServiceUnavailableException ex) {
    ApiError apiError = buildError(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    return new ResponseEntity<>(apiError, HttpStatus.SERVICE_UNAVAILABLE);
  }
}
