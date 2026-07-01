package com.tamim.auth.service.auth;

import com.tamim.auth.enums.AuthProvider;
import com.tamim.auth.enums.RecordStatus;
import com.tamim.auth.model.User;
import com.tamim.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuthUserService {

    private final UserRepository userRepository;

    public User processOAuthUser(OAuth2User oauthUser) {

        String email =
                oauthUser.getAttribute("email");

        String oauthId =
                oauthUser.getName();

        Optional<User> optionalUser =
                userRepository.findByEmailAndStatus(email, RecordStatus.ACTIVE);

        if (optionalUser.isPresent()) {

            User existing = optionalUser.get();

            // update oauth info if needed
            existing.setOauthId(oauthId);

            return userRepository.save(existing);
        }

        User user = new User();

        user.setEmail(email);
        user.setOauthId(oauthId);
        user.setAuthProvider(AuthProvider.GOOGLE);

        user.setEnabled(true);
        user.setEmailVerified(true);

        user.setPasswordHash("");

        return userRepository.save(user);
    }
}
