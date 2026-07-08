package com.siddu.auth.dto.Requests;

import com.siddu.auth.Enums.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class RoleRequest {
    @Email
    @NotBlank(message = "provide email")
    private String email;
    @NotBlank(message ="provide rolename")
    private RoleName role;

}
