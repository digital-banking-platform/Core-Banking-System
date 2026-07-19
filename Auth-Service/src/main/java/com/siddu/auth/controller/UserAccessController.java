package com.siddu.auth.controller;

import com.siddu.auth.dto.Requests.RoleRequest;
import com.siddu.auth.dto.Requests.StatusUpdateRequest;
import com.siddu.auth.dto.Response.RolesResponse;
import com.siddu.auth.dto.Response.StatusUpdateResponse;
import com.siddu.auth.dto.Response.UserStatusResponse;
import com.siddu.auth.service.UserAccessManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

@RestController
public class UserAccessController {
    private final UserAccessManagementService userAccessManagementService;
    @Autowired
    public UserAccessController(UserAccessManagementService userAccessManagementService) {
        this.userAccessManagementService = userAccessManagementService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/admin/users")
    public ResponseEntity<Page<UserStatusResponse>> getUsers(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "15") int size) {
        return  ResponseEntity.ok(userAccessManagementService.getUsers(page,size));

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/users/roles/assign")
    public ResponseEntity<RolesResponse> assignRole(@RequestBody RoleRequest roleAssignRequest) {
        return ResponseEntity.ok(userAccessManagementService.assignRole(roleAssignRequest));

    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/users")
    public ResponseEntity<RolesResponse> revokeRole(@RequestBody RoleRequest roleRevokeRequest) {
        return ResponseEntity.ok(userAccessManagementService.revokeRole(roleRevokeRequest));

    }


    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PatchMapping("/admin/users/status")
     public ResponseEntity<StatusUpdateResponse>  updateStatus(@RequestBody StatusUpdateRequest request){
        return ResponseEntity.ok(userAccessManagementService.updateStatus(request));
    }


}
