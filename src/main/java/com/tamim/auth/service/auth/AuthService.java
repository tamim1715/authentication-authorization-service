package com.tamim.auth.service.auth;

import com.tamim.auth.dto.request.auth.LoginRequest;
import com.tamim.auth.dto.request.auth.RegisterRequest;
import com.tamim.auth.dto.response.AuthResponse;
import com.tamim.auth.enums.RecordStatus;
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
    private final RefreshTokenService refreshTokenService;

    public User register(RegisterRequest request) {
        if (userRepository.existsByEmailAndStatus(
                request.email(), RecordStatus.ACTIVE)) {
            throw new ValidationException("Email already registered");
        }

        User user = UserMapper.toEntity(request, passwordEncoder);
        return userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailAndStatus(request.email(), RecordStatus.ACTIVE)
                .orElseThrow(() -> new ValidationException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ValidationException("Invalid password");
        }

        String accessToken = jwtTokenProvider
                .generateAccessToken(user.getId(), 900000);

        String refreshToken = refreshTokenService
                .generateRefreshToken(user.getId());

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
