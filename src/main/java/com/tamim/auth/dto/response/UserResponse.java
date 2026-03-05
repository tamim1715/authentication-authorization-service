package com.tamim.auth.dto.response;

import com.tamim.auth.enums.UserType;

import java.time.Instant;

public record UserResponse(
        String id,
        String email,
        String firstName,
        String lastName,
        String phone,
        String address,
        UserType userType,
        Instant createdAt
) {
}
