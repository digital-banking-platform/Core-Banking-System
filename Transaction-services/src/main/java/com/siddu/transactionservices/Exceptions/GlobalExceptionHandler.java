package com.siddu.transactionservices.Exceptions;

import com.siddu.dto.transfer.Response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AccessForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleAccessForbiddenException(AccessForbiddenException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).
                body(e.getErrorResponse());

    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).
                body( e.getErrorResponse());
    }
   @ExceptionHandler(BadRequestException.class)
   public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( e.getErrorResponse());
   }

    @ExceptionHandler(AccountInactiveException.class)
    public ResponseEntity<ErrorResponse> handleAccountInactiveException(AccountInactiveException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                body( e.getErrorResponse());
    }
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalanceException(InsufficientBalanceException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                body( e.getErrorResponse());
    }
    @ExceptionHandler(InvalidPinException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPinException(InvalidPinException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                body( e.getErrorResponse());
    }
    @ExceptionHandler(ConcurrentTransactionException.class)
    public ResponseEntity<ErrorResponse> handleConcurrentTransactionException(ConcurrentTransactionException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getErrorResponse());
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

}
