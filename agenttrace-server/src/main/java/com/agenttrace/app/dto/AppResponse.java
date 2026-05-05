package com.agenttrace.app.dto;

import java.time.LocalDateTime;

public record AppResponse(
        Long id,
        String appKey,
        String appName,
        String description,
        String environment,
        Long ownerId,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

