package com.siddu.auth.dto.Response;

import lombok.Getter;

@Getter
public class LogoutResponse {
    private final String message;
    public LogoutResponse(String message) {
        this.message = message;
    }
}
