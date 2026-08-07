package com.siddu.auth.controller;


import com.siddu.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class InternalUserController {
    private final AuthService authService;
    @Autowired
    public InternalUserController(AuthService authService) {
        this.authService = authService;
    }

}
