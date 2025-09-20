package tech.vanguardops.vanguardops.config.security.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import tech.vanguardops.vanguardops.auth.User;

import java.util.Collection;

/**
 * Custom authentication token that holds a JWT and user details.
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final Jwt jwt;
    private final User user;

    public JwtAuthenticationToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities, User user) {
        super(authorities);
        this.jwt = jwt;
        this.user = user;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }
}