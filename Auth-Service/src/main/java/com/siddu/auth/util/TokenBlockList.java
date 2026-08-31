package com.siddu.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class TokenBlockList {

    private final StringRedisTemplate redisTemplate;
    private final SecretKey secretKey;

    TokenBlockList(
            StringRedisTemplate redisTemplate,
            @Value("${jwt.secret}") String secret
    ) {
        this.redisTemplate = redisTemplate;
        this.secretKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );
    }

    public void blockUser(String token, UUID userId) {

        String key = "block-list:user:" + userId;

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Instant expiresAt = claims.getExpiration().toInstant();

        long ttl = Duration.between(
                Instant.now(),
                expiresAt
        ).getSeconds();

        if (ttl > 0) {
            redisTemplate.opsForValue().set(
                    key,
                    "blocked",
                    ttl,
                    TimeUnit.SECONDS
            );
        }
    }

    public void unblockUser( UUID userId){
        String key = "block-list:user:" + userId;
        redisTemplate.delete(key);
    }

}