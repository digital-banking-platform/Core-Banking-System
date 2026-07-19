package com.siddu.accounts.Dto.Responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T>{
    private final T data;
    private final String message;
}
