package com.tamim.auth.service.auth;

import com.tamim.auth.constant.MessageConstants;
import com.tamim.auth.dto.request.auth.LoginRequest;
import com.tamim.auth.dto.request.auth.RegisterRequest;
import com.tamim.auth.dto.response.AuthResponse;
import com.tamim.auth.enums.RecordStatus;
import com.tamim.auth.exception.AuthorizationException;
import com.tamim.auth.exception.ValidationException;
import com.tamim.auth.model.RefreshToken;
import com.tamim.auth.model.User;
import com.tamim.auth.repository.UserRepository;
import com.tamim.auth.security.jwt.JwtTokenProvider;
import com.tamim.auth.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccountLockService accountLockService;
    private final RefreshTokenService refreshTokenService;
    private final EmailVerificationService emailVerificationService;

    public User register(RegisterRequest request) {

        if (userRepository.existsByEmailAndStatus(
                request.email(), RecordStatus.ACTIVE)) {
            throw new ValidationException(MessageConstants.EMAIL_ALREADY_EXISTS);
        }

        // generate request dto to entity
        User userEntity = UserMapper.toEntity(request);

        // password hash for security
        userEntity.setPasswordHash(passwordEncoder.encode(request.password()));
        userEntity.setEnabled(false);
        userEntity.setEmailVerified(false);

        User user = userRepository.save(userEntity);

        // call email verification service for verify user
        emailVerificationService.sendVerificationEmail(user);

        return user;
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmailAndStatus(request.email(), RecordStatus.ACTIVE)
                .orElseThrow(() ->
                        new ValidationException(MessageConstants.INVALID_EMAIL_OR_PASSWORD));

        // check email unverified
        if (!user.isEnabled()) {
            throw new AuthorizationException("Please verify your email first");
        }

        // check account lock
        accountLockService.checkLockStatus(user);

        // compare raw password with hash password
        if (!passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()))
        {
            // increment login failed counter
            accountLockService.loginFailed(user);

            throw new ValidationException(MessageConstants.INVALID_EMAIL_OR_PASSWORD);
        }

        String accessToken = jwtTokenProvider
                .generateAccessToken(user.getId(), 900000);

        String refreshToken = refreshTokenService
                .generateRefreshToken(user.getId());

        // counter reset while successful login
        accountLockService.successLogin(user);

        return new AuthResponse(accessToken, refreshToken, 900);
    }

    public AuthResponse refresh(String refreshToken) {

        // validate + rotation
        RefreshToken oldToken = refreshTokenService
                .validateAndRotate(refreshToken);

        String userId = oldToken.getUserId();

        // generate new access token
        String newAccessToken = jwtTokenProvider
                .generateAccessToken(userId, 900000);

        // generate new refresh token
        String newRefreshToken = refreshTokenService
                .generateRefreshToken(userId);

        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                900
        );
    }

    public void logout(String refreshToken) {

        refreshTokenService
                .revokeToken(refreshToken);
    }
}
