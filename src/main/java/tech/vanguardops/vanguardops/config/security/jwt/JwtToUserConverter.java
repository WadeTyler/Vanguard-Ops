package tech.vanguardops.vanguardops.config.security.jwt;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import tech.vanguardops.vanguardops.auth.User;
import tech.vanguardops.vanguardops.auth.UserService;

/**
 * Converter that transforms a JWT into an AbstractAuthenticationToken containing user details.
 */
@Component
public class JwtToUserConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserService userService;

    public JwtToUserConverter(@Qualifier("userServiceV1") UserService userService) {
        this.userService = userService;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        String username = source.getSubject();
        User user = (User) userService.loadUserByUsername(username);

        return new JwtAuthenticationToken(source, user.getAuthorities(), user);
    }
}