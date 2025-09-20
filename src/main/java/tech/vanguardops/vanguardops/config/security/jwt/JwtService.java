package tech.vanguardops.vanguardops.config.security.jwt;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.vanguardops.vanguardops.auth.User;
import tech.vanguardops.vanguardops.config.AppProperties;

/**
 * Service for generating JWT tokens and authentication cookies.
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;
    private final AppProperties appProperties;

    /**
     * Generates a JWT token for the given user.
     * @param user the user for whom to generate the token
     * @return the generated JWT token
     */
    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("authorities", user.getAuthorities())
                .claim("id", user.getId())
                .issuer(jwtProperties.issuer())
                .expiration(new java.util.Date(System.currentTimeMillis() + jwtProperties.expiresInMs()))
                .signWith(jwtProperties.getSecretKey())
                .compact();
    }

    /**
     * Generates an HTTP-only authentication cookie containing the JWT token.
     * @param token the JWT token to include in the cookie
     * @return the generated authentication cookie
     */
    public Cookie generateAuthCookie(String token) {
        Cookie authCookie = new Cookie("auth_token", token);
        authCookie.setHttpOnly(true);
        authCookie.setSecure(appProperties.isProduction()); // Set to true in production
        authCookie.setPath("/");
        authCookie.setMaxAge((int) (jwtProperties.expiresInMs() / 1000)); // in seconds
        return authCookie;
    }

    /**
     * Generates a cookie that clears the authentication token.
     * @return the cookie that clears the auth token
     */
    public Cookie generateClearAuthCookie() {
        Cookie authCookie = new Cookie("auth_token", "");
        authCookie.setHttpOnly(true);
        authCookie.setSecure(appProperties.isProduction()); // Set to true in production
        authCookie.setPath("/");
        authCookie.setMaxAge(0); // Expire immediately
        return authCookie;
    }

}