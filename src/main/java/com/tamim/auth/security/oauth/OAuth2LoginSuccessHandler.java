package com.tamim.auth.security.oauth;

import com.tamim.auth.model.User;
import com.tamim.auth.security.jwt.JwtTokenProvider;
import com.tamim.auth.service.auth.OAuthUserService;
import com.tamim.auth.service.auth.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final OAuthUserService oauthUserService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oauthUser =
                (OAuth2User) authentication.getPrincipal();

        assert oauthUser != null;
        User user =
                oauthUserService.processOAuthUser(oauthUser);

        String accessToken =
                jwtProvider.generateAccessToken(user.getId(), 900000);

        String refreshToken =
                refreshTokenService.generateRefreshToken(user.getId());

        String redirectUrl =
                "http://localhost:3000/oauth-success"
                        + "?accessToken=" + accessToken
                        + "&refreshToken=" + refreshToken;

        getRedirectStrategy().sendRedirect(
                request,
                response,
                redirectUrl
        );
    }
}
