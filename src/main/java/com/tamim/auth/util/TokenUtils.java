package com.tamim.auth.util;

import com.tamim.auth.constant.MessageConstants;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class TokenUtils {

    public static String generateSecureToken() {
        byte[] randomBytes = new byte[64];
        new SecureRandom().nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    public static String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hash);

        } catch (Exception e) {
            throw new RuntimeException(MessageConstants.TOKEN_HASHING_FAILED);
        }
    }
}
