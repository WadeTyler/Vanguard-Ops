package tech.vanguardops.vanguardops.auth;

import org.springframework.security.core.userdetails.UserDetailsService;
import tech.vanguardops.vanguardops.auth.dto.LoginRequest;
import tech.vanguardops.vanguardops.auth.dto.SignupRequest;

/**
 * Service interface for user authentication and management.
 */
public interface UserService extends UserDetailsService {

    /**
     * Registers a new user based on the provided signup request.
     * @param signupRequest the signup request containing user details
     * @return the registered user
     */
    User signup(SignupRequest signupRequest);

    /**
     * Authenticates a user based on the provided login request.
     * @param loginRequest the login request containing credentials
     * @return the authenticated user
     */
    User login(LoginRequest loginRequest);
}