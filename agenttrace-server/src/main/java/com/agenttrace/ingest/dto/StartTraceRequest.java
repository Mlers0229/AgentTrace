package com.agenttrace.ingest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record StartTraceRequest(
        @NotBlank(message = "appKey is required") String appKey,
        @NotBlank(message = "apiKey is required") String apiKey,
        @NotBlank(message = "traceId is required") @Size(max = 64) String traceId,
        @NotBlank(message = "name is required") @Size(max = 255) String name,
        LocalDateTime startTime,
        String inputSummary
) implements SdkCredentialRequest {
}

