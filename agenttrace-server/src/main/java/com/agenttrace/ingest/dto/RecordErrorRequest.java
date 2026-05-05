package com.agenttrace.ingest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecordErrorRequest(
        @NotBlank(message = "appKey is required") String appKey,
        @NotBlank(message = "apiKey is required") String apiKey,
        @NotBlank(message = "traceId is required") @Size(max = 64) String traceId,
        @Size(max = 64) String spanId,
        @NotBlank(message = "errorType is required") @Size(max = 128) String errorType,
        @NotBlank(message = "errorMessage is required") String errorMessage,
        String stackSummary
) implements SdkCredentialRequest {
}

