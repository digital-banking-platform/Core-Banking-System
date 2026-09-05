package com.siddu.transactionservices.Dto.Response;

import com.siddu.dto.transfer.Response.ErrorResponse;

public class AccessForbiddenResponse implements ErrorResponse {
    String message;
    public AccessForbiddenResponse(String message) {
        this.message = message;
    }
}
