package com.siddu.transactionservices.Exceptions;

import com.siddu.dto.transfer.Response.ErrorResponse;
import lombok.Getter;

@Getter
public class AccessForbiddenException extends RuntimeException {

    private final ErrorResponse errorResponse;

    public AccessForbiddenException(ErrorResponse errorResponse)
    {
       this.errorResponse = errorResponse;
    }


}