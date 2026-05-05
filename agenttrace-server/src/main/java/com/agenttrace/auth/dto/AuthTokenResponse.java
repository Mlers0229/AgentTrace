package com.agenttrace.auth.dto;

public record AuthTokenResponse(
        String tokenType,
        String accessToken,
        long expiresInSeconds,
        UserResponse user
) {
}

