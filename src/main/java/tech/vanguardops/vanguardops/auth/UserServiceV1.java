package tech.vanguardops.vanguardops.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.vanguardops.vanguardops.auth.dto.LoginRequest;
import tech.vanguardops.vanguardops.auth.dto.SignupRequest;
import tech.vanguardops.vanguardops.auth.exception.InvalidUsernamePasswordException;
import tech.vanguardops.vanguardops.auth.exception.UsernameAlreadyExistsException;

@Service("userServiceV1")
@RequiredArgsConstructor
public class UserServiceV1 implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
}