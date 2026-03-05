package com.tamim.auth.service.mapper;

import com.tamim.auth.dto.request.auth.RegisterRequest;
import com.tamim.auth.dto.response.UserResponse;
import com.tamim.auth.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getAddress(),
                user.getUserType(),
                user.getCreatedAt()
        );
    }

    public static User toEntity(RegisterRequest request,
                                PasswordEncoder encoder) {

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(encoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setUserType(request.userType());
        user.setAddress(request.address());
        user.setPhone(request.phone());
        user.setEnabled(true);

        return user;
    }
}
