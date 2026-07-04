package com.siddu.auth.service;

import com.siddu.auth.Enums.*;
import com.siddu.auth.dto.Requests.LoginRequest;
import com.siddu.auth.dto.Requests.RegisterRequest;
import com.siddu.auth.dto.Response.*;
import com.siddu.auth.entity.*;
import com.siddu.auth.exception.EmailAlreadyExistsException;
import com.siddu.auth.exception.InvalidCreditionalException;
import com.siddu.auth.repository.RoleRepository;
import com.siddu.auth.repository.SessionRepository;
import com.siddu.auth.repository.UserRepository;
import com.siddu.auth.repository.UserRoleRepository;
import com.siddu.auth.security.JwtService;
import com.siddu.auth.util.TokenHashUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Service
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
     @Autowired
    public AuthService(UserRepository userRepository, UserRoleRepository userRoleRepository, RoleRepository roleRepository,
                       SessionRepository sessionRepository, PasswordEncoder passwordEncoder, JwtService jwtService
     ) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.sessionRepository = sessionRepository;
         this.passwordEncoder = passwordEncoder;
         this.jwtService = jwtService;

    }

    public AuthResult registerUser(RegisterRequest registerRequest,String deviceInfo,String ipAddress) {
         if(userRepository.existsByEmail(registerRequest.getEmail())) {
             throw new EmailAlreadyExistsException("Email already exists");
         }

        UserEntity user = UserEntity.builder()
                .email(registerRequest.getEmail())
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .status(UserStatus.ACTIVE)
                .build();

        user=userRepository.save(user);

        RoleEntity role=roleRepository.findByName(RoleName.USER).orElseThrow(() -> new IllegalStateException("role user not found"));

        UserRoleEntity userRole = UserRoleEntity.builder()
                .user(user)
                .role(role)
                .build();
        userRoleRepository.save(userRole);

        List<String> roles = userRoleRepository.findByUser(user)
                .stream()
                .map(ur -> ur.getRole().getName().name())
                .toList();
        String  accessToken=jwtService.generateAccessToken(user.getId(),user.getEmail(),roles);

        String refreshToken =jwtService.generateRefreshToken(user.getId());
        String Hashed_refreshToken= TokenHashUtil.hash(refreshToken);

        SessionEntity session = SessionEntity.builder()
                .user(user)
                .refreshTokenHash(Hashed_refreshToken)   // NEVER store raw token
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .isActive(true)
                .expiresAt(Instant.now().plusSeconds(7 * 24 * 60 * 60)) // 7 days
                .build();

        sessionRepository.save(session);

        return new AuthResult(new UserResponse(user.getEmail(),roles),accessToken,refreshToken);

    }

    public AuthResult loginUser(LoginRequest loginRequest,String deviceInfo,String ipAddress) {
        UserEntity user=userRepository.findByEmail(loginRequest.getEmail()).
                orElseThrow(() -> new InvalidCreditionalException("invalid credentials"));

        if(!passwordEncoder.matches(loginRequest.getPassword(),user.getPasswordHash())) {
            throw new InvalidCreditionalException("invalid credentials");
        }

        sessionRepository.deactivateAllByUser(user.getId());

        List<String> roles = userRoleRepository.findByUser(user)
                .stream()
                .map(ur -> ur.getRole().getName().name())
                .toList();

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(),roles);
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        SessionEntity session=SessionEntity.builder()
                .user(user)
                .refreshTokenHash(TokenHashUtil.hash(refreshToken))
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .isActive(true)
                .expiresAt(Instant.now().plusSeconds(7 * 24 * 60 * 60))
                .build();

        sessionRepository.save(session);

        return new AuthResult(new UserResponse(user.getEmail(),roles),accessToken,refreshToken);

    }

    public LogoutResponse logoutUser(UUID userId) {
         sessionRepository.deactivateAllByUser(userId);
         return new LogoutResponse("logout successfully");

    }


}
