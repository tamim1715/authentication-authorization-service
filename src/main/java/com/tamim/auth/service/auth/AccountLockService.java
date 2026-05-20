package com.tamim.auth.service.auth;

import com.tamim.auth.constant.AppConstants;
import com.tamim.auth.constant.MessageConstants;
import com.tamim.auth.exception.AuthorizationException;
import com.tamim.auth.model.User;
import com.tamim.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountLockService {

    private final UserRepository userRepository;

    public void loginFailed(User user) {

        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

        if (user.getFailedLoginAttempts() >= AppConstants.LOGIN_FAILED_MAX_ATTEMPTS) {

            user.setAccountLockedUntil(
                    Instant.now()
                            .plus(Duration.ofMinutes(
                                    AppConstants.LOCK_DURATION_MINUTES
                            ))
            );
            user.setFailedLoginAttempts(0);
        }

        userRepository.save(user);
    }

    public void successLogin(User user) {
        user.setAccountLockedUntil(null);
        user.setFailedLoginAttempts(0);

        userRepository.save(user);
    }

    public void checkLockStatus(User user) {

        if (user.getAccountLockedUntil() == null) {
            return;
        }

        // auto unlock
        if (user.getAccountLockedUntil()
                .isBefore(Instant.now())) {

            user.setAccountLockedUntil(null);

            userRepository.save(user);

            return;
        }

        throw new AuthorizationException(MessageConstants.ACCOUNT_LOCK);
    }
}
