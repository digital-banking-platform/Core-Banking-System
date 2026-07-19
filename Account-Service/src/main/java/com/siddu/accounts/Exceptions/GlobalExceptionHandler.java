package com.siddu.accounts.Exceptions;


import com.siddu.accounts.Dto.Responses.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

}
