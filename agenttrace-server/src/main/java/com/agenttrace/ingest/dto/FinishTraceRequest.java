package com.agenttrace.ingest.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record FinishTraceRequest(
        @NotBlank(message = "appKey is required") String appKey,
        @NotBlank(message = "apiKey is required") String apiKey,
        @NotBlank(message = "status is required") String status,
        LocalDateTime endTime,
        String outputSummary,
        String errorMessage
) implements SdkCredentialRequest {
}

