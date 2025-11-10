package com.snapshot.lumina.businesslogic.presentation.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
@Slf4j
public class CookieUtil {
    public static final String JWT_COOKIE_NAME = "auth_token";

    public Optional<String> extractJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) {
            log.warn("No cookies found in request");
            return Optional.empty();
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> JWT_COOKIE_NAME.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .filter(value -> !value.isEmpty());
    }

}
