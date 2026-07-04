package com.siddu.auth.dto.Response;

import lombok.Getter;

@Getter
public class AuthResult {

    private final UserResponse userResponse;
    private final String token;
    private final String refreshToken;
    public AuthResult(UserResponse userResponse, String token, String refreshToken) {
        this.userResponse = userResponse;
        this.token = token;
        this.refreshToken = token;
    }

}