package tech.vanguardops.vanguardops.auth;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tech.vanguardops.vanguardops.auth.dto.LoginRequest;
import tech.vanguardops.vanguardops.auth.dto.SignupRequest;
import tech.vanguardops.vanguardops.auth.dto.UserDTO;
import tech.vanguardops.vanguardops.config.security.jwt.JwtService;

/**
 * Controller for authentication endpoints.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthControllerV1 {

    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    public AuthControllerV1(@Qualifier("userServiceV1") UserService userService, UserMapper userMapper, JwtService jwtService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
    }

    @GetMapping
    public UserDTO getAuthUser(@AuthenticationPrincipal User user) {
        System.out.println(user.getAuthorities());
        return userMapper.toDTO(user);
    }

    @PostMapping("/signup")
    public UserDTO signup(@RequestBody @Valid SignupRequest signupRequest, HttpServletResponse response) {
        User user = userService.signup(signupRequest);
        String token = jwtService.generateToken(user);
        response.addCookie(jwtService.generateAuthCookie(token));
        return userMapper.toDTO(user);
    }

    @PostMapping("/login")
    public UserDTO login(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse response) {
        User user = userService.login(loginRequest);
        String token = jwtService.generateToken(user);
        response.addCookie(jwtService.generateAuthCookie(token));
        return userMapper.toDTO(user);
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        response.addCookie(jwtService.generateClearAuthCookie());
    }


}