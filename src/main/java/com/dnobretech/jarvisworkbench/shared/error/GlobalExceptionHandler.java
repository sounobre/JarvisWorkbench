package com.dnobretech.jarvisworkbench.shared.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {

        ApiErrorResponse apiErrorResponse = buildError(
                HttpStatus.NOT_FOUND,
                ApiErrorCode.RESOURCE_NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI(),
                List.of());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiErrorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<ApiFieldError> fields = new ArrayList<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(fieldError -> {
                    fields.add(
                            new ApiFieldError(
                                    fieldError.getField(),
                                    fieldError.getDefaultMessage()

                            ));
                });

        ApiErrorResponse apiErrorResponse = buildError(
                HttpStatus.BAD_REQUEST,
                ApiErrorCode.VALIDATION_ERROR,
                "Invalid request data",
                request.getRequestURI(),
                fields);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        List<ApiFieldError> fields = new ArrayList<>();

        ex.getConstraintViolations()
                .forEach(constraintViolation -> {

                    String fieldName = constraintViolation.getPropertyPath().toString();

                    if (fieldName.contains(".")) {
                        fieldName = fieldName.substring(fieldName.lastIndexOf('.') + 1);
                    }
                    fields.add(
                            new ApiFieldError(
                                    fieldName,
                                    constraintViolation.getMessage())
                    );

                });

        ApiErrorResponse apiErrorResponse = buildError(
                HttpStatus.BAD_REQUEST,
                ApiErrorCode.VALIDATION_ERROR,
                "Invalid request parameters",
                request.getRequestURI(),
                fields);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        ApiErrorResponse apiErrorResponse = buildError(
                HttpStatus.BAD_REQUEST,
                ApiErrorCode.INVALID_REQUEST,
                "Malformed request body",
                request.getRequestURI(),
                List.of());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiErrorResponse);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request) {

        ApiErrorResponse apiErrorResponse = buildError(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ApiErrorCode.BUSINESS_ERROR,
                ex.getMessage(),
                request.getRequestURI(),
                List.of());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(apiErrorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected internal error", ex);

        ApiErrorResponse apiErrorResponse = buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ApiErrorCode.INTERNAL_ERROR,
                "Unexpected internal error",
                request.getRequestURI(),
                List.of());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiErrorResponse);
    }

    private ApiErrorResponse buildError(
            HttpStatus status,
            ApiErrorCode errorCode,
            String message,
            String path,
            List<ApiFieldError> fields
    ) {
        return new ApiErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                errorCode.name(),
                message,
                path,
                fields == null ? List.of() : fields
        );
    }
}
