package com.agenttrace.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAppRequest(
        @NotBlank(message = "appName is required")
        @Size(max = 128, message = "appName must be at most 128 characters")
        String appName,

        @Size(max = 500, message = "description must be at most 500 characters")
        String description,

        @Size(max = 32, message = "environment must be at most 32 characters")
        String environment
) {
}

