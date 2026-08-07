package com.siddu.transactionservices.Exceptions;

import com.siddu.dto.transfer.Response.ErrorResponse;
import lombok.Getter;

@Getter
public class ConcurrentTransactionException extends RuntimeException {

    private final ErrorResponse errorResponse;

    public ConcurrentTransactionException(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }


}