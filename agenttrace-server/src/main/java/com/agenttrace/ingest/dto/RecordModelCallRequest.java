package com.agenttrace.ingest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RecordModelCallRequest(
        @NotBlank(message = "appKey is required") String appKey,
        @NotBlank(message = "apiKey is required") String apiKey,
        @NotBlank(message = "traceId is required") @Size(max = 64) String traceId,
        @NotBlank(message = "spanId is required") @Size(max = 64) String spanId,
        @NotBlank(message = "provider is required") @Size(max = 64) String provider,
        @NotBlank(message = "modelName is required") @Size(max = 128) String modelName,
        @PositiveOrZero Integer promptTokens,
        @PositiveOrZero Integer completionTokens,
        @PositiveOrZero Integer totalTokens,
        @PositiveOrZero BigDecimal cost,
        @PositiveOrZero Long latencyMs,
        @NotBlank(message = "status is required") String status,
        String errorMessage
) implements SdkCredentialRequest {
}

