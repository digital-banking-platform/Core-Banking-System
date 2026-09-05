package com.siddu.apigateway.ratelimit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
public class UserRateLimiter {

    private static final long MAX_REQUESTS = 25;
    private static final Duration WINDOW = Duration.ofMinutes(2);

    private final StringRedisTemplate redisTemplate;

    public UserRateLimiter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(UUID userId) {

        String key = "rate-limit:user:" + userId;

        Long count = redisTemplate.opsForValue().increment(key);

        if (count == null) {
            return false;
        }

        if (count == 1) {
            redisTemplate.expire(key, WINDOW);
        }

        return count <= MAX_REQUESTS;
    }
}