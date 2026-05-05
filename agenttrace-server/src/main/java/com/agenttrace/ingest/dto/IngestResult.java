package com.agenttrace.ingest.dto;

public record IngestResult(
        Long id,
        String traceId,
        String spanId,
        String status,
        boolean created
) {
}

