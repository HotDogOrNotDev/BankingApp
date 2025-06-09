package com.aston.frontendpracticeservice.service;

import com.aston.frontendpracticeservice.domain.request.AuthRequest;
import com.aston.frontendpracticeservice.domain.response.JwtResponse;

public interface AuthService {

    JwtResponse authAndGetToken(AuthRequest authRequest);

    JwtResponse getAccessToken(String refreshToken);

}
