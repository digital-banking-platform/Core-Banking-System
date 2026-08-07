package com.siddu.transactionservices.Exceptions;

import com.siddu.dto.transfer.Response.ErrorResponse;
import com.siddu.transactionservices.Dto.Response.TransferMoneyResponse;
import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final ErrorResponse errorResponse;

    public ResourceNotFoundException(ErrorResponse errorResponse) {

    this.errorResponse = errorResponse;
    }
}
