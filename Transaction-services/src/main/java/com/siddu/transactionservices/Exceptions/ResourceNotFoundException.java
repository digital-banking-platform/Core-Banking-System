package com.siddu.transactionservices.Exceptions;

import com.siddu.dto.transfer.Response.ErrorResponse;
import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final ErrorResponse errorResponse;

    public ResourceNotFoundException(ErrorResponse errorResponse) {

    this.errorResponse = errorResponse;
    }
}
