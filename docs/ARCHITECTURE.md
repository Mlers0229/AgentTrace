# AgentTrace Architecture

## Purpose

AgentTrace records, stores, queries, and analyzes execution traces for AI Agent applications. A single user request is represented as one Trace. Each Agent step, tool call, model call, or system step is represented as a Span or related event record.

## High-Level Flow

```text
AI Agent Application
  -> AgentTrace Java SDK
  -> AgentTrace API Server
  -> PostgreSQL
  -> Redis
  -> Web Dashboard
```

## Runtime Components

### AgentTrace Java SDK

The SDK is embedded in external AI Agent applications. It provides lightweight reporting methods:

- Start Trace
- Finish Trace
- Start Span
- Finish Span
- Record model call
- Record tool call
- Record error

The SDK authenticates with `appKey` and `apiKey`.

### AgentTrace API Server

The server receives SDK events, persists records, exposes user-facing query APIs, and provides dashboard statistics.

Planned backend modules:

```text
agenttrace-server
  auth          login, registration, JWT, current user
  app           application management and API key rotation
  ingest        SDK reporting endpoints
  trace         Trace creation, completion, query
  span          Span creation, completion, tree query
  model         model call records
  tool          tool call records
  error         error records
  dashboard     aggregate statistics
  common        shared response, exception, security, config
```

### PostgreSQL

PostgreSQL stores users, applications, traces, spans, model calls, tool calls, and errors. JSON request and response payloads are stored as `jsonb`.

### Redis

Redis is reserved for lightweight statistics, cache, rate limiting, and future hot dashboard data. The MVP can start with PostgreSQL-first statistics and add Redis only where it clearly reduces repeated computation.

### Web Dashboard

The frontend lets developers manage applications, inspect traces, view execution timelines, analyze errors, and review token/cost metrics.

## Authentication Model

### User APIs

User-facing APIs use JWT:

- Login
- Application management
- Trace query
- Dashboard query
- Error management

### SDK Ingestion APIs

SDK ingestion APIs use application credentials:

- `appKey`
- `apiKey`

The raw API key is only returned when created or rotated. The server stores only the key hash.

## Core Data Relationships

```text
sys_user 1 -> N trace_app
trace_app 1 -> N trace_record
trace_record 1 -> N trace_span
trace_record 1 -> N model_call_record
trace_record 1 -> N tool_call_record
trace_record 1 -> N error_record
trace_span 1 -> N model_call_record
trace_span 1 -> N tool_call_record
trace_span 1 -> N error_record
```

## MVP Boundary

The first version should complete:

- User login and JWT authentication
- Application creation and API key management
- SDK ingestion for Trace, Span, model call, tool call, and error records
- Trace list and detail query
- Dashboard overview statistics
- Docker Compose local deployment

Do not include OpenTelemetry compatibility, WebSocket live logs, alerts, AI error analysis, or multi-tenant billing in the MVP.

