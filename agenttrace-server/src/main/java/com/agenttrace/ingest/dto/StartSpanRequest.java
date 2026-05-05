package com.agenttrace.ingest.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record StartSpanRequest(
        @NotBlank(message = "appKey is required") String appKey,
        @NotBlank(message = "apiKey is required") String apiKey,
        @NotBlank(message = "traceId is required") @Size(max = 64) String traceId,
        @NotBlank(message = "spanId is required") @Size(max = 64) String spanId,
        @Size(max = 64) String parentSpanId,
        @NotBlank(message = "spanType is required") String spanType,
        @NotBlank(message = "name is required") @Size(max = 255) String name,
        LocalDateTime startTime,
        JsonNode inputPayload
) implements SdkCredentialRequest {
}

