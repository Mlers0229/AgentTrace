package com.agenttrace.app.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateAppStatusRequest(
        @NotBlank(message = "status is required")
        String status
) {
}

