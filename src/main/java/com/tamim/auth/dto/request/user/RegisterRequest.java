package com.tamim.auth.dto.request.user;

import com.tamim.auth.enums.UserType;
import jakarta.validation.constraints.*;

public record RegisterRequest(
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
        String phone,

        String address, //optional

        @NotNull(message = "User type is required")
        UserType userType
) {}
