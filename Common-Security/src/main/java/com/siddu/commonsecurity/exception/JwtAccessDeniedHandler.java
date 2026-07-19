package com.siddu.commonsecurity.exception;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;

    @Component
    public class JwtAccessDeniedHandler implements AccessDeniedHandler {

        private final ObjectMapper mapper = new ObjectMapper();

        @Override
        public void handle(
                HttpServletRequest request,
                HttpServletResponse response,
                 AccessDeniedException ex
        ) throws IOException {

            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");

            Map<String, Object> body = Map.of(
                    "status", 403,
                    "error", "FORBIDDEN",
                    "message", "Access denied",
                    "path", request.getRequestURI(),
                    "timestamp", Instant.now().toString()
            );

            mapper.writeValue(response.getOutputStream(), body);
        }
    }

