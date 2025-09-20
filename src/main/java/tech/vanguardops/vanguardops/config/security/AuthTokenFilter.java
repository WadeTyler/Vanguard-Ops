package tech.vanguardops.vanguardops.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter to extract the auth token from the cookies and add it as a header to the request
 */
public class AuthTokenFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Add Auth token as a header to the request from the cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equalsIgnoreCase("auth_token")) {
                    request = new HttpServletRequestWrapper(request) {
                        @Override
                        public String getHeader(String name) {
                            if (name.equalsIgnoreCase("Authorization")) {
                                return "Bearer " + cookie.getValue();
                            }
                            return super.getHeader(name);
                        }
                    };
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);

    }
}