package com.siddu.transactionservices.Dto.Response;

public record ApiErrorResponse(
        String error,
        String message
) {
}
