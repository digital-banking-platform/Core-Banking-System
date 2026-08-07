package com.siddu.transactionservices.Exceptions;

import com.siddu.dto.transfer.Response.ErrorResponse;
import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
    private final ErrorResponse errorResponse;
    public BadRequestException(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;

    }
}
