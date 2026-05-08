package com.tamim.auth.dto.request.auth;

public record ResetPasswordRequest(
        String token,
        String newPassword
) {
}
