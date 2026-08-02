package com.siddu.transactionservices.Dto.Response;

public record ApiErrorResponse<T>(
        String error,
        T data
) {
}
