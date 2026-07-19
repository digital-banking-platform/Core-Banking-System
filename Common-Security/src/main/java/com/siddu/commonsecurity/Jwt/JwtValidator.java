package com.siddu.commonsecurity.Jwt;

import com.siddu.commonsecurity.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtValidator {
    private final SecretKey key;
    public JwtValidator(JwtProperties jwtProperties) {
        this.key = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtProperties.getSecret())
        );
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isRefreshTokenValid(String refreshToken) {
        try {
            Claims claims = extractAllClaims(refreshToken);

            return "REFRESH".equals(claims.get("type", String.class))
                    && !isExpired(claims);

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }


    public UUID extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return UUID.fromString(claims.getSubject());
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);

        Object rolesObj = claims.get("roles");

        if (rolesObj instanceof List<?> roles) {
            return roles.stream()
                    .map(String::valueOf)
                    .toList();
        }

        return List.of();
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}
