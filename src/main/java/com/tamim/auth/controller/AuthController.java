package com.tamim.auth.controller;

import com.tamim.auth.dto.request.auth.LoginRequest;
import com.tamim.auth.dto.request.auth.RegisterRequest;
import com.tamim.auth.dto.response.AuthResponse;
import com.tamim.auth.dto.response.UserResponse;
import com.tamim.auth.model.User;
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
@RequestMapping("/api/v1/auth")
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

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
