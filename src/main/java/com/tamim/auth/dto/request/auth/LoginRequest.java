package com.tamim.auth.dto.request.auth;

public record LoginRequest (
        String email,
        String password
) {
}
