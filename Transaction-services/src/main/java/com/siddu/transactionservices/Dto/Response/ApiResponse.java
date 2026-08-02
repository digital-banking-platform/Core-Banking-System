package com.siddu.transactionservices.Dto.Response;

public record ApiResponse<T>(
        T data,
        String message
) {

}
