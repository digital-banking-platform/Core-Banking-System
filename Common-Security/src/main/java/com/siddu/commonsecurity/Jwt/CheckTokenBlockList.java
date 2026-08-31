package com.siddu.commonsecurity.Jwt;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CheckTokenBlockList {
    private final StringRedisTemplate redisTemplate;

    public CheckTokenBlockList(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isUserLoggedOut(UUID userId) {
        String key = "block-list:user:" + userId;
        return redisTemplate.hasKey(key);
    }
}
