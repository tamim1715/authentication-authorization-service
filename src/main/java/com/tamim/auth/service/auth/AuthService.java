package com.tamim.auth.service.auth;

import com.tamim.auth.dto.request.user.RegisterRequest;
import com.tamim.auth.enums.RecordStatus;
import com.tamim.auth.exception.ValidationException;
import com.tamim.auth.model.user.User;
import com.tamim.auth.repository.UserRepository;
import com.tamim.auth.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequest request) {

        if (userRepository.existsByEmailAndStatus(
                request.email(), RecordStatus.ACTIVE)) {
            throw new ValidationException("Email already registered");
        }

        User user = UserMapper.toEntity(request, passwordEncoder);
        return userRepository.save(user);
    }
}
