package com.siddu.auth.controller;

import com.siddu.dto.pinvalidation.Request.PinValidationRequest;
import com.siddu.dto.pinvalidation.Response.PinValidationResponse;
import com.siddu.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InternalUserController {
    private final AuthService authService;
    @Autowired
    public InternalUserController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/internal/pin/validate")
    public ResponseEntity<PinValidationResponse> validatePin(@RequestBody PinValidationRequest request){
        return  ResponseEntity.ok(authService.validatepin(request));

    }

}
