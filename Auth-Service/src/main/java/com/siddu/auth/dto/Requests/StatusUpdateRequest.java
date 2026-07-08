package com.siddu.auth.dto.Requests;


import com.siddu.auth.Enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StatusUpdateRequest {
    private String email;
    private UserStatus status;
}
