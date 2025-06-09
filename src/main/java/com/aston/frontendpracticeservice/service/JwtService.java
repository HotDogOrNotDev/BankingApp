package com.aston.frontendpracticeservice.service;

import com.aston.frontendpracticeservice.domain.entity.User;
import com.aston.frontendpracticeservice.security.JwtAuthentication;
import io.jsonwebtoken.Claims;
import lombok.NonNull;

import java.security.Key;
import java.util.Set;

public interface JwtService {

    String generateAccessToken(@NonNull User user);

    Set<String> extractRoles(User user);

    String generateRefreshToken(@NonNull User user);

    boolean validateAccessToken(@NonNull String accessToken);

    boolean validateRefreshToken(@NonNull String refreshToken);

    Claims getAccessClaims(@NonNull String token);

    Claims getRefreshClaims(@NonNull String token);

    Claims getClaims(@NonNull String token);

    boolean validateToken(@NonNull String token);

    Key getSignKey();

    JwtAuthentication generate(Claims claims);

}
