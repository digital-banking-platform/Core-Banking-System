package com.siddu.auth.controller;

import com.siddu.auth.dto.Requests.LoginRequest;
import com.siddu.auth.dto.Requests.RegisterRequest;
import com.siddu.auth.dto.Response.LogoutResponse;
import com.siddu.auth.dto.Response.TokenResponse;
import com.siddu.auth.dto.Response.UserResponse;
import com.siddu.auth.dto.Response.AuthResult;
import com.siddu.auth.service.AuthService;
import com.siddu.commonsecurity.Jwt.JwtValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.siddu.auth.util.CookieUtil;
import java.util.UUID;

@RestController
public class AuthController {
    private final AuthService authService;
    private final CookieUtil cookieutil;
    private final JwtValidator jwtValidator;

    public AuthController(AuthService authService, CookieUtil cookieutil, JwtValidator jwtValidator) {
        this.authService = authService;
        this.cookieutil=cookieutil;
        this.jwtValidator = jwtValidator;
    }
    @PostMapping("/Auth/register")
    public ResponseEntity<UserResponse> RegisterUser(@Valid @RequestBody RegisterRequest registerRequest,
                                                     HttpServletResponse response, HttpServletRequest request) {
        String deviceInfo=request.getHeader("User-Agent");
        String ipAddress=request.getRemoteAddr();
        AuthResult authresult=authService.registerUser(registerRequest,deviceInfo,ipAddress);
        cookieutil.addAccessToken(response,authresult.getToken());
        cookieutil.addRefreshToken(response,authresult.getRefreshToken());
        return ResponseEntity.ok(authresult.getUserResponse());

    }

    @PostMapping("/Auth/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest loginRequest,
                                              HttpServletResponse response, HttpServletRequest request) {
        String deviceInfo=request.getHeader("User-Agent");
        String ipAddress=request.getRemoteAddr();
        AuthResult authResult=authService.loginUser(loginRequest,deviceInfo,ipAddress);
        cookieutil.addAccessToken(response,authResult.getToken());
        cookieutil.addRefreshToken(response,authResult.getRefreshToken());
        return ResponseEntity.ok(authResult.getUserResponse());
    }

    @PostMapping("Auth/logout")
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("Authorization") String authheader,HttpServletResponse response) {
        String token=authheader.substring(7);
        UUID  userid=jwtValidator.extractUserId(token);
        LogoutResponse logoutResponse = authService.logoutUser(userid);
        cookieutil.clearAccessToken(response);
        cookieutil.clearRefreshToken(response);
        return ResponseEntity.ok(logoutResponse);

    }

    @PostMapping("Auth/refresh")
    public ResponseEntity<?> rotateRefreshToken(@CookieValue("REFRESH_TOKEN") String refreshToken,HttpServletResponse response) {
        TokenResponse tokenresponse=authService.rotateRefreshToken(refreshToken);
        cookieutil.addAccessToken(response,tokenresponse.getToken());
        cookieutil.addRefreshToken(response,tokenresponse.getRefreshToken());
        return ResponseEntity.ok("refresh token rotated successfully");

    }
    @PutMapping("/Auth/reset-password")
    public void resetPassword(){

    }

    @PutMapping("Auth/change-password")
    public void changePassword(){

    }
}
