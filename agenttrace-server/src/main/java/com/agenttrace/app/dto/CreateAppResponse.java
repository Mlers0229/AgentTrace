package com.agenttrace.app.dto;

public record CreateAppResponse(
        AppResponse app,
        String apiKey
) {
}

