package com.siddu.accounts.Utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

public final class SecurityUtils {
    private SecurityUtils() {}
    public static UUID getCurrentUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UUID)authentication.getPrincipal();

    }
    public static List<String> getCurrentUserRoles() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }

    public static boolean hasRole(String role) {
        return getCurrentUserRoles().contains("ROLE_" + role);
    }

}
