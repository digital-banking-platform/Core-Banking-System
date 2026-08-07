package com.siddu.transactionservices.Exceptions;

import com.siddu.dto.transfer.Response.ErrorResponse;
import lombok.Getter;

@Getter
public class InsufficientBalanceException extends RuntimeException {
    private final ErrorResponse errorResponse;

    public InsufficientBalanceException(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;

    }
}
