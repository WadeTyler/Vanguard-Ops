package tech.vanguardops.vanguardops.auth;

import org.springframework.security.core.userdetails.UserDetailsService;
import tech.vanguardops.vanguardops.auth.dto.LoginRequest;
import tech.vanguardops.vanguardops.auth.dto.ManageUserRequest;
import tech.vanguardops.vanguardops.auth.dto.SignupRequest;

import java.util.List;

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

    /**
     * Retrieves a list of all users.
     * @return a list of all users
     */
    List<User> getAllUsers();

    /**
     * Retrieves a user by their unique identifier.
     * @param id the unique identifier of the user
     * @return the user with the specified id
     */
    User getUserById(String id);

    /**
     * Updates the details of an existing user.
     * @param id the unique identifier of the user to be updated
     * @param manageUserRequest the request containing updated user details
     * @return the updated user
     */
    User updateUser(String id, ManageUserRequest manageUserRequest);
}