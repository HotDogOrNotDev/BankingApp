package com.aston.frontendpracticeservice.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(name = "Response for access token and refresh token")
public record JwtResponse(String accessToken, String refreshToken) {

    public JwtResponse {
    }

}
