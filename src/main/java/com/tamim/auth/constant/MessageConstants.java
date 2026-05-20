package com.tamim.auth.constant;

public final class MessageConstants {

    private MessageConstants() {
    }

    // Exception Messages
    public static final String TOKEN_EXPIRED = "Token expired";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String TOKEN_ALREADY_USED = "Token already used";
    public static final String INVALID_REFRESH_TOKEN = "Invalid refresh token";
    public static final String REFRESH_TOKEN_EXPIRED = "Refresh token expired";
    public static final String TOKEN_HASHING_FAILED = "Token hashing failed";
    public static final String EMAIL_ALREADY_EXISTS = "Email already registered";
    public static final String INVALID_OR_EXPIRED_TOKEN = "Invalid or expired token";
    public static final String INVALID_EMAIL_OR_PASSWORD = "Invalid email or password";
    public static final String ACCOUNT_LOCK = "Account locked. Try again later.";
    public static final String REFRESH_TOKEN_REUSE_DETECTED = "Refresh token reuse detected. All sessions revoked.";
}
