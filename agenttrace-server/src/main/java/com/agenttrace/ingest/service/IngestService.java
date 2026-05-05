package com.agenttrace.ingest.service;

import com.agenttrace.app.entity.TraceApp;
import com.agenttrace.error.entity.ErrorRecord;
import com.agenttrace.error.mapper.ErrorRecordMapper;
import com.agenttrace.ingest.dto.FinishSpanRequest;
import com.agenttrace.ingest.dto.FinishTraceRequest;
import com.agenttrace.ingest.dto.IngestResult;
import com.agenttrace.ingest.dto.RecordErrorRequest;
import com.agenttrace.ingest.dto.RecordModelCallRequest;
import com.agenttrace.ingest.dto.RecordToolCallRequest;
import com.agenttrace.ingest.dto.StartSpanRequest;
import com.agenttrace.ingest.dto.StartTraceRequest;
import com.agenttrace.model.entity.ModelCallRecord;
import com.agenttrace.model.mapper.ModelCallRecordMapper;
import com.agenttrace.span.entity.TraceSpan;
import com.agenttrace.span.mapper.TraceSpanMapper;
import com.agenttrace.tool.entity.ToolCallRecord;
import com.agenttrace.tool.mapper.ToolCallRecordMapper;
import com.agenttrace.trace.entity.TraceRecord;
import com.agenttrace.trace.mapper.TraceRecordMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

@Service
public class IngestService {

    private static final Set<String> TRACE_FINISH_STATUSES = Set.of("SUCCESS", "FAILED");
    private static final Set<String> EVENT_STATUSES = Set.of("SUCCESS", "FAILED");
    private static final Set<String> SPAN_TYPES = Set.of("AGENT", "MODEL", "TOOL", "SYSTEM");

    private final SdkAuthService authService;
    private final TraceRecordMapper traceMapper;
    private final TraceSpanMapper spanMapper;
    private final ModelCallRecordMapper modelCallMapper;
    private final ToolCallRecordMapper toolCallMapper;
    private final ErrorRecordMapper errorRecordMapper;
    private final ObjectMapper objectMapper;

    public IngestService(
            SdkAuthService authService,
            TraceRecordMapper traceMapper,
            TraceSpanMapper spanMapper,
            ModelCallRecordMapper modelCallMapper,
            ToolCallRecordMapper toolCallMapper,
            ErrorRecordMapper errorRecordMapper,
            ObjectMapper objectMapper
    ) {
        this.authService = authService;
        this.traceMapper = traceMapper;
        this.spanMapper = spanMapper;
        this.modelCallMapper = modelCallMapper;
        this.toolCallMapper = toolCallMapper;
        this.errorRecordMapper = errorRecordMapper;
        this.objectMapper = objectMapper;
    }

    public IngestResult startTrace(StartTraceRequest request) {
        TraceApp app = authService.authenticate(request);
        TraceRecord existing = findTrace(request.traceId());
        if (existing != null) {
            ensureTraceBelongsToApp(existing, app);
            return new IngestResult(existing.getId(), existing.getTraceId(), null, existing.getStatus(), false);
        }

        LocalDateTime now = LocalDateTime.now();
        TraceRecord trace = new TraceRecord();
        trace.setTraceId(request.traceId());
        trace.setAppId(app.getId());
        trace.setName(request.name());
        trace.setStatus("RUNNING");
        trace.setStartTime(request.startTime() == null ? now : request.startTime());
        trace.setInputSummary(request.inputSummary());
        trace.setCreatedAt(now);
        traceMapper.insert(trace);
        return new IngestResult(trace.getId(), trace.getTraceId(), null, trace.getStatus(), true);
    }

    public IngestResult finishTrace(String traceId, FinishTraceRequest request) {
        TraceApp app = authService.authenticate(request);
        requireStatus(request.status(), TRACE_FINISH_STATUSES);
        TraceRecord trace = requireTrace(traceId, app);
        if (trace.getEndTime() != null) {
            return new IngestResult(trace.getId(), trace.getTraceId(), null, trace.getStatus(), false);
        }

        LocalDateTime endTime = request.endTime() == null ? LocalDateTime.now() : request.endTime();
        trace.setStatus(request.status());
        trace.setEndTime(endTime);
        trace.setDurationMs(Duration.between(trace.getStartTime(), endTime).toMillis());
        trace.setOutputSummary(request.outputSummary());
        trace.setErrorMessage(request.errorMessage());
        traceMapper.updateById(trace);
        return new IngestResult(trace.getId(), trace.getTraceId(), null, trace.getStatus(), false);
    }

    public IngestResult startSpan(StartSpanRequest request) {
        TraceApp app = authService.authenticate(request);
        requireStatus(request.spanType(), SPAN_TYPES);
        requireTrace(request.traceId(), app);
        TraceSpan existing = findSpan(request.spanId());
        if (existing != null) {
            ensureSpanBelongsToTrace(existing, request.traceId());
            return new IngestResult(existing.getId(), existing.getTraceId(), existing.getSpanId(), existing.getStatus(), false);
        }

        TraceSpan span = new TraceSpan();
        span.setTraceId(request.traceId());
        span.setSpanId(request.spanId());
        span.setParentSpanId(request.parentSpanId());
        span.setSpanType(request.spanType());
        span.setName(request.name());
        span.setStatus("RUNNING");
        span.setStartTime(request.startTime() == null ? LocalDateTime.now() : request.startTime());
        span.setInputPayload(toJson(request.inputPayload()));
        spanMapper.insert(span);
        return new IngestResult(span.getId(), span.getTraceId(), span.getSpanId(), span.getStatus(), true);
    }

    public IngestResult finishSpan(String spanId, FinishSpanRequest request) {
        TraceApp app = authService.authenticate(request);
        requireStatus(request.status(), EVENT_STATUSES);
        TraceSpan span = requireSpan(spanId);
        requireTrace(span.getTraceId(), app);
        if (span.getEndTime() != null) {
            return new IngestResult(span.getId(), span.getTraceId(), span.getSpanId(), span.getStatus(), false);
        }

        LocalDateTime endTime = request.endTime() == null ? LocalDateTime.now() : request.endTime();
        span.setStatus(request.status());
        span.setEndTime(endTime);
        span.setDurationMs(Duration.between(span.getStartTime(), endTime).toMillis());
        span.setOutputPayload(toJson(request.outputPayload()));
        span.setErrorMessage(request.errorMessage());
        spanMapper.updateById(span);
        return new IngestResult(span.getId(), span.getTraceId(), span.getSpanId(), span.getStatus(), false);
    }

    public IngestResult recordModelCall(RecordModelCallRequest request) {
        TraceApp app = authService.authenticate(request);
        requireStatus(request.status(), EVENT_STATUSES);
        requireTrace(request.traceId(), app);
        requireSpanInTrace(request.spanId(), request.traceId());

        ModelCallRecord record = new ModelCallRecord();
        record.setTraceId(request.traceId());
        record.setSpanId(request.spanId());
        record.setProvider(request.provider());
        record.setModelName(request.modelName());
        record.setPromptTokens(defaultInt(request.promptTokens()));
        record.setCompletionTokens(defaultInt(request.completionTokens()));
        record.setTotalTokens(request.totalTokens() == null
                ? defaultInt(request.promptTokens()) + defaultInt(request.completionTokens())
                : request.totalTokens());
        record.setCost(request.cost() == null ? BigDecimal.ZERO : request.cost());
        record.setLatencyMs(defaultLong(request.latencyMs()));
        record.setStatus(request.status());
        record.setErrorMessage(request.errorMessage());
        record.setCreatedAt(LocalDateTime.now());
        modelCallMapper.insert(record);
        return new IngestResult(record.getId(), record.getTraceId(), record.getSpanId(), record.getStatus(), true);
    }

    public IngestResult recordToolCall(RecordToolCallRequest request) {
        TraceApp app = authService.authenticate(request);
        requireStatus(request.status(), EVENT_STATUSES);
        requireTrace(request.traceId(), app);
        requireSpanInTrace(request.spanId(), request.traceId());

        ToolCallRecord record = new ToolCallRecord();
        record.setTraceId(request.traceId());
        record.setSpanId(request.spanId());
        record.setToolName(request.toolName());
        record.setToolType(request.toolType());
        record.setRequestPayload(toJson(request.requestPayload()));
        record.setResponsePayload(toJson(request.responsePayload()));
        record.setLatencyMs(defaultLong(request.latencyMs()));
        record.setStatus(request.status());
        record.setErrorMessage(request.errorMessage());
        record.setCreatedAt(LocalDateTime.now());
        toolCallMapper.insert(record);
        return new IngestResult(record.getId(), record.getTraceId(), record.getSpanId(), record.getStatus(), true);
    }

    public IngestResult recordError(RecordErrorRequest request) {
        TraceApp app = authService.authenticate(request);
        requireTrace(request.traceId(), app);
        if (request.spanId() != null && !request.spanId().isBlank()) {
            requireSpanInTrace(request.spanId(), request.traceId());
        }

        ErrorRecord record = new ErrorRecord();
        record.setTraceId(request.traceId());
        record.setSpanId(request.spanId());
        record.setErrorType(request.errorType());
        record.setErrorMessage(request.errorMessage());
        record.setStackSummary(request.stackSummary());
        record.setResolved(false);
        record.setCreatedAt(LocalDateTime.now());
        errorRecordMapper.insert(record);
        return new IngestResult(record.getId(), record.getTraceId(), record.getSpanId(), "RECORDED", true);
    }

    private TraceRecord findTrace(String traceId) {
        return traceMapper.selectOne(new LambdaQueryWrapper<TraceRecord>().eq(TraceRecord::getTraceId, traceId));
    }

    private TraceRecord requireTrace(String traceId, TraceApp app) {
        TraceRecord trace = findTrace(traceId);
        if (trace == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "trace not found");
        }
        ensureTraceBelongsToApp(trace, app);
        return trace;
    }

    private TraceSpan findSpan(String spanId) {
        return spanMapper.selectOne(new LambdaQueryWrapper<TraceSpan>().eq(TraceSpan::getSpanId, spanId));
    }

    private TraceSpan requireSpan(String spanId) {
        TraceSpan span = findSpan(spanId);
        if (span == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "span not found");
        }
        return span;
    }

    private TraceSpan requireSpanInTrace(String spanId, String traceId) {
        TraceSpan span = requireSpan(spanId);
        ensureSpanBelongsToTrace(span, traceId);
        return span;
    }

    private void ensureTraceBelongsToApp(TraceRecord trace, TraceApp app) {
        if (!trace.getAppId().equals(app.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "trace does not belong to application");
        }
    }

    private void ensureSpanBelongsToTrace(TraceSpan span, String traceId) {
        if (!span.getTraceId().equals(traceId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "span does not belong to trace");
        }
    }

    private void requireStatus(String value, Set<String> allowed) {
        if (!allowed.contains(value)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid status or type: " + value);
        }
    }

    private String toJson(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(node);
        } catch (JsonProcessingException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid json payload");
        }
    }

    private int defaultInt(Integer value) {
        return value == null ? 0 : value;
    }

    private long defaultLong(Long value) {
        return value == null ? 0 : value;
    }
}

