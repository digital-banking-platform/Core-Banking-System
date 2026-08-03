package com.siddu.transactionservices.Exceptions;

import com.siddu.transactionservices.Dto.Response.ApiErrorResponse;
import com.siddu.transactionservices.Dto.Response.TransferMoneyResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AccessForbiddenException.class)
    public ResponseEntity<ApiErrorResponse<TransferMoneyResponse>> handleAccessForbiddenException(AccessForbiddenException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).
                body(new ApiErrorResponse<>(HttpStatus.FORBIDDEN.name(), e.getMoneyResponse()));

    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse<TransferMoneyResponse>> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).
                body(new ApiErrorResponse<>(HttpStatus.NOT_FOUND.name(),  e.getMoneyResponse()));
    }
   @ExceptionHandler(BadRequestException.class)
   public ResponseEntity<ApiErrorResponse<TransferMoneyResponse>> handleBadRequestException(BadRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrorResponse<>(HttpStatus.BAD_REQUEST.name(),  e.getMoneyResponse()));
   }

    @ExceptionHandler(AccountInactiveException.class)
    public ResponseEntity<ApiErrorResponse<TransferMoneyResponse>> handleAccountInactiveException(AccountInactiveException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                body(new ApiErrorResponse<>(HttpStatus.BAD_REQUEST.name(),  e.getMoneyResponse()));
    }
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiErrorResponse<TransferMoneyResponse>> handleInsufficientBalanceException(InsufficientBalanceException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                body(new ApiErrorResponse<>(HttpStatus.BAD_REQUEST.name(),  e.getMoneyResponse()));
    }
    @ExceptionHandler(InvalidPinException.class)
    public ResponseEntity<ApiErrorResponse<TransferMoneyResponse>> handleInvalidPinException(InvalidPinException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                body(new ApiErrorResponse<>(HttpStatus.BAD_REQUEST.name(),  e.getMoneyResponse()));
    }
    @ExceptionHandler(ConcurrentTransactionException.class)
    public ResponseEntity<ApiErrorResponse<TransferMoneyResponse>> handleConcurrentTransactionException(ConcurrentTransactionException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiErrorResponse<>(HttpStatus.CONFLICT.name(),e.getResponse()));
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

}
