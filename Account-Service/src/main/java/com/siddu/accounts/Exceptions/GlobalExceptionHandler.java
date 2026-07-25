package com.siddu.accounts.Exceptions;


import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.siddu.accounts.Dto.Responses.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleAccountAlreadyExistsException(AccountAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).
                body(new ApiErrorResponse(HttpStatus.CONFLICT.name(),e.getMessage()));

    }

    @ExceptionHandler(KycMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleKycMismatchException(KycMismatchException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                body(new ApiErrorResponse(HttpStatus.BAD_REQUEST.name(),e.getMessage()));
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).
                body(new ApiErrorResponse(HttpStatus.NOT_FOUND.name(),e.getMessage()));
    }
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleAccountNotFoundException(AccountNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).
                body(new ApiErrorResponse(HttpStatus.NOT_FOUND.name(),e.getMessage()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = Objects.requireNonNull(e.getBindingResult()
                        .getFieldError())
                .getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                body(new ApiErrorResponse(HttpStatus.BAD_REQUEST.name(),errorMessage));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {

        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException invalid &&
                invalid.getTargetType().isEnum()) {

            String field = invalid.getPath().get(0).getFieldName();

            String message = "Invalid value for '" + field ;

            return ResponseEntity.badRequest()
                    .body(new ApiErrorResponse(HttpStatus.BAD_REQUEST.name(), message));
        }

        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse(HttpStatus.BAD_REQUEST.name(), "Invalid request body"));
    }
    @ExceptionHandler(AccessForbiddenException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessForbiddenException(AccessForbiddenException e) {
        return  ResponseEntity.status(HttpStatus.FORBIDDEN).
                body(new ApiErrorResponse(HttpStatus.FORBIDDEN.name(),e.getMessage()));
    }
    @ExceptionHandler(DuplicateResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateResourceFoundException(DuplicateResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).
                body(new ApiErrorResponse(HttpStatus.CONFLICT.name(),e.getMessage()));
    }
    @ExceptionHandler(InvalidPinException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidPinException(InvalidPinException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                body(new ApiErrorResponse(HttpStatus.BAD_REQUEST.name(),e.getMessage()));
    }



}
