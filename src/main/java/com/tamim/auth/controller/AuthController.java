package com.tamim.auth.controller;

import com.tamim.auth.dto.request.user.RegisterRequest;
import com.tamim.auth.dto.response.UserResponse;
import com.tamim.auth.model.user.User;
import com.tamim.auth.service.auth.AuthService;
import com.tamim.auth.service.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        User user = authService.register(request);
        UserResponse response = UserMapper.toResponse(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
