package com.siddu.transactionservices.Exceptions;

import com.siddu.dto.transfer.Response.ErrorResponse;
import lombok.Getter;

@Getter
public class AccountInactiveException extends RuntimeException {
    private final ErrorResponse errorResponse;
    public AccountInactiveException(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }
}
