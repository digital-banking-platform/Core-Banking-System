package com.siddu.apigateway.ratelimit;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.UUID;

public class RateLimiterFilter {

    private static UserRateLimiter userRateLimiter;
    private static RateLimitExceededHandler rateLimitExceededHandler;

    public RateLimiterFilter(
            UserRateLimiter userRateLimiter,
            RateLimitExceededHandler rateLimitExceededHandler
    ) {
        RateLimiterFilter.userRateLimiter = userRateLimiter;
        RateLimiterFilter.rateLimitExceededHandler = rateLimitExceededHandler;
    }

    public static HandlerFilterFunction<ServerResponse, ServerResponse> rateLimiter() {

        return (request, next) -> {

            Authentication authentication =
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication();

            if (authentication == null ||
                    !authentication.isAuthenticated()) {

                return next.handle(request);
            }

            UUID userId =
                    (UUID) authentication.getPrincipal();

            if (!userRateLimiter.isAllowed(userId)) {

                return rateLimitExceededHandler.handle(request);
            }

            return next.handle(request);
        };
    }
}