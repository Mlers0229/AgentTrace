package com.agenttrace.ingest.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record RecordToolCallRequest(
        @NotBlank(message = "appKey is required") String appKey,
        @NotBlank(message = "apiKey is required") String apiKey,
        @NotBlank(message = "traceId is required") @Size(max = 64) String traceId,
        @NotBlank(message = "spanId is required") @Size(max = 64) String spanId,
        @NotBlank(message = "toolName is required") @Size(max = 128) String toolName,
        @NotBlank(message = "toolType is required") @Size(max = 64) String toolType,
        JsonNode requestPayload,
        JsonNode responsePayload,
        @PositiveOrZero Long latencyMs,
        @NotBlank(message = "status is required") String status,
        String errorMessage
) implements SdkCredentialRequest {
}

