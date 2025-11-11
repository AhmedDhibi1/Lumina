package lumina.snapshot.authservice.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    @Value("${jwt.cookie.access-token.name}")
    private String accessTokenName;

    @Value("${jwt.cookie.access-token.max-age}")
    private int accessTokenMaxAge;

    @Value("${jwt.cookie.refresh-token.name}")
    private String refreshTokenName;

    @Value("${jwt.cookie.refresh-token.max-age}")
    private int refreshTokenMaxAge;

    @Value("${jwt.cookie.access-token.http-only}")
    private boolean httpOnly;

    @Value("${jwt.cookie.access-token.secure}")
    private boolean secure;

    @Value("${jwt.cookie.access-token.same-site}")
    private String sameSite;

    public void addAccessTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = createCookie(accessTokenName, token, accessTokenMaxAge);
        response.addCookie(cookie);
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = createCookie(refreshTokenName, token, refreshTokenMaxAge);
        response.addCookie(cookie);
    }

    public void clearAccessTokenCookie(HttpServletResponse response) {
        Cookie cookie = createCookie(accessTokenName, "", 0);
        response.addCookie(cookie);
    }

    public void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = createCookie(refreshTokenName, "", 0);
        response.addCookie(cookie);
    }

    private Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }
}