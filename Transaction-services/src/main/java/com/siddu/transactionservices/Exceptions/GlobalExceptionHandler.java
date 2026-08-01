package com.siddu.transactionservices.Exceptions;

import com.siddu.transactionservices.Dto.Response.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AccessForbiddenException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessForbiddenException(AccessForbiddenException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).
                body(new ApiErrorResponse(HttpStatus.FORBIDDEN.name(),  e.getMessage()));

    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).
                body(new ApiErrorResponse(HttpStatus.NOT_FOUND.name(),  e.getMessage()));
    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequestException(BadRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                body(new ApiErrorResponse(HttpStatus.BAD_REQUEST.name(),  e.getMessage()));

    }

    @ExceptionHandler(AccountInactiveException.class)
    public ResponseEntity<ApiErrorResponse> handleAccountInactiveException(AccountInactiveException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                body(new ApiErrorResponse(HttpStatus.BAD_REQUEST.name(),  e.getMessage()));
    }
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficientBalanceException(InsufficientBalanceException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                body(new ApiErrorResponse(HttpStatus.BAD_REQUEST.name(),  e.getMessage()));
    }
    @ExceptionHandler(InvalidPinException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidPinException(InvalidPinException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                body(new ApiErrorResponse(HttpStatus.BAD_REQUEST.name(),  e.getMessage()));
    }

}
