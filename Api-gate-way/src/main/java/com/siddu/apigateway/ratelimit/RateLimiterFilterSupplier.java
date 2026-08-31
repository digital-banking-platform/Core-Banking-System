package com.siddu.apigateway.ratelimit;

import org.springframework.cloud.gateway.server.mvc.filter.SimpleFilterSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterFilterSupplier
        extends SimpleFilterSupplier {

    public RateLimiterFilterSupplier() {
        super(RateLimiterFilter.class);
    }

    @Bean
    public RateLimiterFilter rateLimiterFilter(
            UserRateLimiter userRateLimiter,
            RateLimitExceededHandler rateLimitExceededHandler
    ) {
        return new RateLimiterFilter(
                userRateLimiter,
                rateLimitExceededHandler
        );
    }
}