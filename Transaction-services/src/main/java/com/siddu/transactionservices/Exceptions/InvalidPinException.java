package com.siddu.transactionservices.Exceptions;

import com.siddu.dto.transfer.Response.ErrorResponse;
import com.siddu.transactionservices.Dto.Response.TransferMoneyResponse;
import lombok.Getter;

@Getter
public class InvalidPinException extends RuntimeException {
    private final ErrorResponse errorResponse;

    public InvalidPinException(ErrorResponse  errorResponse) {

       this.errorResponse = errorResponse;
    }
}
