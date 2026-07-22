package com.phatakp.kpevents.common.exceptions;

import com.phatakp.kpevents.common.dto.ApiError;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicateResources(DuplicateResourceException ex) {
        String errorCode = "DUPLICATE_"+ex.getResourceName().toUpperCase();
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of(errorCode, ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
        String errorCode = ex.getResourceName().toUpperCase()+"_NOT_FOUND";
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of(errorCode, ex.getMessage()));
    }

    @ExceptionHandler(ActionNotAllowedException.class)
    public ResponseEntity<ApiError> handleNotAllowed(ActionNotAllowedException ex) {
        String errorCode = "FORBIDDEN";
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiError.of(errorCode, ex.getMessage()));
    }


    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiError> handleBusinessRuleError(BusinessRuleException ex) {
        String errorCode = ex.getErrorCode();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of(errorCode, ex.getMessage()));
    }



    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException ex) {
        String errorCode = "UNAUTHORIZED";
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiError.of(errorCode, ex.getMessage()));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiError> handleJwtException(JwtException ex) {
        String errorCode = "UNAUTHORIZED";
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiError.of(errorCode, ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex) {
        String errorCode = "UNAUTHORIZED";
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiError.of(errorCode, ex.getMessage()));
    }



    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ApiError> handleTransactionSystemException(
            TransactionSystemException ex, HttpServletRequest request) {

        Throwable cause = ex.getRootCause();


        // Check if the root cause was a Jakarta Validation Constraint failure
        if (cause instanceof ConstraintViolationException constraintEx) {
            List<ApiError.FieldError> generalErrors = new ArrayList<>();
            constraintEx.getConstraintViolations().forEach(violation -> {
                ApiError.FieldError errors = new ApiError.FieldError(violation.getPropertyPath().toString(),
                        violation.getMessage());
                generalErrors.add(errors);
            });

            String errorCode = "INVALID_DATA";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiError.of(errorCode, ex.getMessage(),generalErrors));
        }
        String errorCode = "INVALID_DATA";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of(errorCode, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex) {

        List<ApiError.FieldError> generalErrors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            if (error instanceof FieldError fieldErr) {
                ApiError.FieldError errors = new ApiError.FieldError(fieldErr.getField(), fieldErr.getDefaultMessage());
                generalErrors.add(errors);
            } else {
                ApiError.FieldError errors = new ApiError.FieldError("error", error.getDefaultMessage());
                generalErrors.add(errors);
            }
        });
        String errorCode = "INVALID_DATA";
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(ApiError.of(errorCode, ex.getMessage(),generalErrors));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleException(MethodArgumentTypeMismatchException ex) {
        String errorCode = "INVALID_DATA";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of(errorCode, ex.getMessage()));
    }



    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleException(HttpMessageNotReadableException ex) {
        String errorCode = "INVALID_DATA";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of(errorCode, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleError(Exception ex) {
        String errorCode = "INTERNAL_SERVER_ERROR";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.of(errorCode, ex.getMessage()));
    }
}
