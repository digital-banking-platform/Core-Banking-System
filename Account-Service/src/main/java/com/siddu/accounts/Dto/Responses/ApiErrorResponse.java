package com.siddu.accounts.Dto.Responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiErrorResponse {
    private String message;
    private String error;

}
