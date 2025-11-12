package com.snapshot.lumina.businesslogic.infrastructure.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final JwtValidator jwtValidator;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Extract JWT from current request
        String jwt = extractJwtFromCurrentRequest();

        if (jwt == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // Extract roles from JWT
        List<GrantedAuthority> authorities = extractAuthorities(jwt);

        return User.builder()
                .username(username)
                .password("")  // Not used
                .authorities(authorities)
                .build();
    }

    private String extractJwtFromCurrentRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            if (request.getCookies() != null) {
                return Arrays.stream(request.getCookies())
                        .filter(cookie -> "accessToken".equals(cookie.getName()))
                        .map(Cookie::getValue)
                        .findFirst()
                        .orElse(null);
            }
        }
        return null;
    }

    private List<GrantedAuthority> extractAuthorities(String jwt) {
        // Parse JWT and extract roles from claims
        // Implementation depends on how roles are stored in JWT
        // Typically: realm_access.roles + resource_access.{client}.roles

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));  // Default role

        // TODO: Extract actual roles from JWT claims

        return authorities;
    }
}
