package com.tamim.auth.service;

import com.tamim.auth.dto.request.auth.LoginRequest;
import com.tamim.auth.dto.response.AuthResponse;
import com.tamim.auth.enums.RecordStatus;
import com.tamim.auth.exception.AuthorizationException;
import com.tamim.auth.exception.ValidationException;
import com.tamim.auth.model.User;
import com.tamim.auth.repository.UserRepository;
import com.tamim.auth.security.jwt.JwtTokenProvider;
import com.tamim.auth.service.auth.AccountLockService;
import com.tamim.auth.service.auth.AuthService;
import com.tamim.auth.service.auth.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AccountLockService accountLockService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_success() {
        User user = new User();
        user.setId("tamim1234");
        user.setEmail("tamim@gmail.com");
        user.setPasswordHash("password");
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setFailedLoginAttempts(0);

        when(passwordEncoder.matches("password", "password"))
                .thenReturn(true);

        when(userRepository.findByEmailAndStatus("tamim@gmail.com", RecordStatus.ACTIVE))
                .thenReturn(Optional.of(user));

        when(jwtProvider.generateAccessToken("tamim1234", 900000))
                .thenReturn("access-token");

        when(refreshTokenService.generateRefreshToken("tamim1234"))
                .thenReturn("refresh-token");

        LoginRequest request =
                new LoginRequest("tamim@gmail.com", "password");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("access-token", response.accessToken());
    }

    @Test
    void login_user_not_found() {
        when(userRepository.findByEmailAndStatus("x@mail.com", RecordStatus.ACTIVE))
                .thenReturn(Optional.empty());

        LoginRequest request =
                new LoginRequest("x@mail.com", "pass");

        assertThrows(ValidationException.class,
                () -> authService.login(request));
    }

    @Test
    void user_disable() {
        User user = new User();
        user.setEnabled(false);
        user.setEmail("x@mail.com");

        when(userRepository.findByEmailAndStatus("x@mail.com", RecordStatus.ACTIVE))
                .thenReturn(Optional.of(user));

        LoginRequest request =
                new LoginRequest("x@mail.com", "pass");

        assertThrows(AuthorizationException.class,
                () -> authService.login(request));
    }
}
