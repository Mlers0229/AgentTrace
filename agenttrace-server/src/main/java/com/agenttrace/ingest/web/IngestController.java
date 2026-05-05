package com.agenttrace.ingest.web;

import com.agenttrace.common.api.ApiResponse;
import com.agenttrace.ingest.dto.FinishSpanRequest;
import com.agenttrace.ingest.dto.FinishTraceRequest;
import com.agenttrace.ingest.dto.IngestResult;
import com.agenttrace.ingest.dto.RecordErrorRequest;
import com.agenttrace.ingest.dto.RecordModelCallRequest;
import com.agenttrace.ingest.dto.RecordToolCallRequest;
import com.agenttrace.ingest.dto.StartSpanRequest;
import com.agenttrace.ingest.dto.StartTraceRequest;
import com.agenttrace.ingest.service.IngestService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingest")
public class IngestController {

    private final IngestService ingestService;

    public IngestController(IngestService ingestService) {
        this.ingestService = ingestService;
    }

    @PostMapping("/traces/start")
    public ApiResponse<IngestResult> startTrace(@Valid @RequestBody StartTraceRequest request) {
        return ApiResponse.success(ingestService.startTrace(request));
    }

    @PostMapping("/traces/{traceId}/finish")
    public ApiResponse<IngestResult> finishTrace(
            @PathVariable String traceId,
            @Valid @RequestBody FinishTraceRequest request
    ) {
        return ApiResponse.success(ingestService.finishTrace(traceId, request));
    }

    @PostMapping("/spans/start")
    public ApiResponse<IngestResult> startSpan(@Valid @RequestBody StartSpanRequest request) {
        return ApiResponse.success(ingestService.startSpan(request));
    }

    @PostMapping("/spans/{spanId}/finish")
    public ApiResponse<IngestResult> finishSpan(
            @PathVariable String spanId,
            @Valid @RequestBody FinishSpanRequest request
    ) {
        return ApiResponse.success(ingestService.finishSpan(spanId, request));
    }

    @PostMapping("/model-calls")
    public ApiResponse<IngestResult> recordModelCall(@Valid @RequestBody RecordModelCallRequest request) {
        return ApiResponse.success(ingestService.recordModelCall(request));
    }

    @PostMapping("/tool-calls")
    public ApiResponse<IngestResult> recordToolCall(@Valid @RequestBody RecordToolCallRequest request) {
        return ApiResponse.success(ingestService.recordToolCall(request));
    }

    @PostMapping("/errors")
    public ApiResponse<IngestResult> recordError(@Valid @RequestBody RecordErrorRequest request) {
        return ApiResponse.success(ingestService.recordError(request));
    }
}

