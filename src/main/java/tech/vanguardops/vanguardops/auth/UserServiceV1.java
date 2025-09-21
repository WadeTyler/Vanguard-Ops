package tech.vanguardops.vanguardops.auth;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.vanguardops.vanguardops.auth.dto.LoginRequest;
import tech.vanguardops.vanguardops.auth.dto.ManageUserRequest;
import tech.vanguardops.vanguardops.auth.dto.SignupRequest;
import tech.vanguardops.vanguardops.auth.exception.InvalidUsernamePasswordException;
import tech.vanguardops.vanguardops.auth.exception.UserNotFoundException;
import tech.vanguardops.vanguardops.auth.exception.UsernameAlreadyExistsException;
import tech.vanguardops.vanguardops.config.AppProperties;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service("userServiceV1")
@RequiredArgsConstructor
public class UserServiceV1 implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;

    @PostConstruct
    public void init() {
        createDefaultAdminUser();
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }

    @Override
    @Transactional
    public User signup(SignupRequest signupRequest) {
        // Check if existing username
        if (userRepository.existsByUsernameIgnoreCase(signupRequest.username())) {
            throw new UsernameAlreadyExistsException("Email already exists.");
        }

        // Create user
        User user = User.builder()
                .username(signupRequest.username())
                .passwordHash(passwordEncoder.encode(signupRequest.password()))
                .firstName(signupRequest.firstName())
                .lastName(signupRequest.lastName())
                .authority(new Authority("ROLE_ANALYST"))
                .build();

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User login(LoginRequest loginRequest) {
        // Find existing user
        User user = (User) loadUserByUsername(loginRequest.username());

        // Verify password match
        if (!passwordEncoder.matches(loginRequest.password(), user.getPasswordHash())) {
            throw new InvalidUsernamePasswordException("Invalid Email or Password.");
        }

        return user;
    }

    @Override
    @Transactional
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User getUserById(String id) {
        return userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new UserNotFoundException("User not found."));
    }

    @Transactional
    @Override
    public User updateUser(String id, ManageUserRequest manageUserRequest) {
        // Find existing user
        User existingUser = getUserById(id);
        // Only update mutable fields
        existingUser.setFirstName(manageUserRequest.firstName());
        existingUser.setLastName(manageUserRequest.lastName());
        existingUser.setAuthority(new Authority(manageUserRequest.authority()));
        return userRepository.save(existingUser);
    }

    /**
     * Creates a default admin user if none exists and the application is not in production mode.
     */
    private void createDefaultAdminUser() {
        if (!appProperties.isProduction()) {
            if (!userRepository.existsByAuthority_Authority("ROLE_ADMIN")) {
                String adminPassword = "Admin@1234"; // Default password for the admin user
                User adminUser = User.builder()
                        .username("admin@email.com")
                        .passwordHash(passwordEncoder.encode(adminPassword))
                        .firstName("Admin")
                        .lastName("User")
                        .authority(new Authority("ROLE_ADMIN"))
                        .build();
                userRepository.save(adminUser);

                log.info("UserServiceV1: Default admin user created with\nUsername: {}\nPassword: {}", adminUser.getUsername(), adminPassword);
            } else {
                log.info("UserServiceV1: Admin user already exists. Skipping default admin user creation.");
            }

        }
    }
}