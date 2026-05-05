package com.agenttrace.app.dto;

public record RotateKeyResponse(
        Long appId,
        String appKey,
        String apiKey
) {
}

