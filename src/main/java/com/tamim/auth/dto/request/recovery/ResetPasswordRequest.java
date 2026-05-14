package com.tamim.auth.dto.request.recovery;

public record ResetPasswordRequest(
        String token,
        String newPassword
) {
}
