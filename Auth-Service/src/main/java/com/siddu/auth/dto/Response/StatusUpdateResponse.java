package com.siddu.auth.dto.Response;

import com.siddu.auth.Enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StatusUpdateResponse {
    private String email;
    private UserStatus status;
    private String message;
}
