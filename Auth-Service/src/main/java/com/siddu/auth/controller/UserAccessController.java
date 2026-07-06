package com.siddu.auth.controller;

import com.siddu.auth.dto.Requests.RoleAssignRequest;
import com.siddu.auth.dto.Response.RolesResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
@RestController("/admin")
public class UserAccessController {


    GetMapping("/users")
    public ResponseEntity<RolesResponse> getUsers() {

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/roles")
    public ResponseEntity<RolesResponse>  Assignroles(@RequestBody RoleAssignRequest){
        return   ResponseEntity.ok().body(new RolesResponse());

    }

}
