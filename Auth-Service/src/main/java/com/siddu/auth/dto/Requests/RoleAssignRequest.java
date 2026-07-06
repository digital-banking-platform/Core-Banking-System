package com.siddu.auth.dto.Requests;

import com.siddu.auth.Enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoleAssignRequest {

    RoleName role;
}
