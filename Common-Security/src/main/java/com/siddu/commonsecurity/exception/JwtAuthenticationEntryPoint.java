package com.siddu.commonsecurity.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;

    @Component
    public class JwtAuthenticationEntryPoint
            implements AuthenticationEntryPoint {

        private final ObjectMapper mapper = new ObjectMapper();

        @Override
        public void commence(
                HttpServletRequest request,
                HttpServletResponse response,
                AuthenticationException authException
        ) throws IOException {

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");

            Map<String, Object> body = Map.of(
                    "status", 401,
                    "error", "UNAUTHORIZED",
                    "message", "Authentication required",
                    "path", request.getRequestURI(),
                    "timestamp", Instant.now().toString()
            );

            mapper.writeValue(response.getOutputStream(), body);
        }
    }

