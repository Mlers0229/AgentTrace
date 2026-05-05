package com.agenttrace.ingest.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record FinishSpanRequest(
        @NotBlank(message = "appKey is required") String appKey,
        @NotBlank(message = "apiKey is required") String apiKey,
        @NotBlank(message = "status is required") String status,
        LocalDateTime endTime,
        JsonNode outputPayload,
        String errorMessage
) implements SdkCredentialRequest {
}

