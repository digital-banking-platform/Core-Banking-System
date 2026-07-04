package com.siddu.auth.util;
import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CookieUtil {

    public void addAccessToken(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("ACCESS_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);
    }

    public void addRefreshToken(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("REFRESH_TOKEN", token);

        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(cookie);
    }
}