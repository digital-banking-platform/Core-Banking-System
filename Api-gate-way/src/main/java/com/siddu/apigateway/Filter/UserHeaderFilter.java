package com.siddu.apigateway.Filter;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.List;
import java.util.UUID;

@Component
public class UserHeaderFilter {

    public static HandlerFilterFunction<ServerResponse, ServerResponse> addUserHeaders() {

        return (request, next) -> {


            Authentication authentication =
                    SecurityContextHolder.getContext()
                            .getAuthentication();



            if (authentication == null || !authentication.isAuthenticated()) {
                return next.handle(request);
            }

            UUID userId = (UUID) authentication.getPrincipal();

            List<String> roles =
                    authentication.getAuthorities()
                            .stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList();


            ServerRequest modifiedRequest =
                    ServerRequest.from(request)
                            .header("X-User-Id", userId.toString())
                            .header("X-User-Roles", String.join(",", roles))
                            .headers(headers ->
                                    headers.remove(HttpHeaders.AUTHORIZATION)
                            )
                            .build();

            return next.handle(modifiedRequest);
        };
    }
}