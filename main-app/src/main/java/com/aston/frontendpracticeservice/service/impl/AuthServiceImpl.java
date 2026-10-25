package com.aston.frontendpracticeservice.service.impl;

import com.aston.frontendpracticeservice.domain.entity.User;
import com.aston.frontendpracticeservice.domain.request.AuthRequest;
import com.aston.frontendpracticeservice.domain.response.JwtResponse;
import com.aston.frontendpracticeservice.exception.AuthException;
import com.aston.frontendpracticeservice.service.AuthService;
import com.aston.frontendpracticeservice.service.JwtService;
import com.aston.frontendpracticeservice.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    public static final String INCORRECT_PASSWORD = "Incorrect password";

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, String> refreshStorage = new ConcurrentHashMap<>();

    @Override
    public JwtResponse authAndGetToken(AuthRequest authRequest) {
        final User user = userService.findByLogin(authRequest.login());

        if (!passwordEncoder.matches(authRequest.password(), user.getPassword())) {
            throw new AuthException(INCORRECT_PASSWORD);
        }

        final String accessToken = jwtService.generateAccessToken(user);
        final String refreshToken = jwtService.generateRefreshToken(user);
        refreshStorage.put(user.getLogin(), refreshToken);

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    @Override
    public JwtResponse getAccessToken(String refreshToken) {
        if (jwtService.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtService.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final User user = userService.findByLogin(login);
                final String accessToken = jwtService.generateAccessToken(user);
                return new JwtResponse(accessToken, jwtService.generateRefreshToken(user));
            }
        }
        return new JwtResponse(null, null);
    }

}

