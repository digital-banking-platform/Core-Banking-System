package com.siddu.accounts.Dto.Responses;


public record ApiResponse<T>
        (T data, String message) { }
