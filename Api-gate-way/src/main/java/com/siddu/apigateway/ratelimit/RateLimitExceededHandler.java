package com.siddu.apigateway.ratelimit;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.time.Instant;
import java.util.Map;

@Component
public class RateLimitExceededHandler {

    public ServerResponse handle(ServerRequest request) {

        Map<String, Object> body = Map.of(
                "status", HttpStatus.TOO_MANY_REQUESTS.value(),
                "error", "TOO_MANY_REQUESTS",
                "message", "Too many attempts. Please try again later.",
                "path", request.path(),
                "timestamp", Instant.now().toString()
        );

        return ServerResponse
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }
}