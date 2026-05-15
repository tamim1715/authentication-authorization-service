package com.tamim.auth.controller;

import com.tamim.auth.dto.request.verification.ResendVerificationRequest;
import com.tamim.auth.dto.request.verification.VerifyEmailRequest;
import com.tamim.auth.service.auth.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/verify-email")
    public void verify(@RequestBody VerifyEmailRequest request) {

        emailVerificationService.verify(request.token());
    }

    @PostMapping("/resend-verification")
    public void resendVerification(@RequestBody ResendVerificationRequest request) {

        emailVerificationService.resendVerificationEmail(request.email());
    }
}
