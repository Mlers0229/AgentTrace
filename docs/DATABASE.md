# AgentTrace Database Design

## Database

- PostgreSQL is the primary database.
- Flyway manages schema migrations.
- Java timestamp fields use `LocalDateTime`.
- Cost fields use `BigDecimal`.
- JSON request and response payloads are stored as PostgreSQL `jsonb` and mapped as `String` in the first implementation.

## Table Overview

| Table | Responsibility |
| --- | --- |
| `sys_user` | Platform users and roles |
| `trace_app` | Applications connected to AgentTrace |
| `trace_record` | One full request execution trace |
| `trace_span` | One execution node inside a trace |
| `model_call_record` | Model invocation metrics and status |
| `tool_call_record` | Tool invocation metrics and status |
| `error_record` | Error details linked to traces and spans |

## `sys_user`

| Field | Notes |
| --- | --- |
| `id` | Primary key |
| `username` | Unique username |
| `password_hash` | Hashed password |
| `email` | User email |
| `role` | `ADMIN`, `DEVELOPER`, `VIEWER` |
| `status` | `ENABLED`, `DISABLED` |
| `created_at` | Created time |
| `updated_at` | Updated time |

## `trace_app`

| Field | Notes |
| --- | --- |
| `id` | Primary key |
| `app_key` | Public application key used by SDK |
| `app_name` | Display name |
| `description` | Application description |
| `environment` | `DEV`, `TEST`, `PROD`, or custom value |
| `api_key_hash` | Hashed SDK API key |
| `owner_id` | Owner user ID |
| `status` | `ENABLED`, `DISABLED` |
| `created_at` | Created time |
| `updated_at` | Updated time |

## `trace_record`

| Field | Notes |
| --- | --- |
| `id` | Primary key |
| `trace_id` | Unique trace ID |
| `app_id` | Related application ID |
| `name` | Trace name |
| `status` | `RUNNING`, `SUCCESS`, `FAILED` |
| `start_time` | Trace start time |
| `end_time` | Trace end time |
| `duration_ms` | Total duration |
| `input_summary` | Input summary |
| `output_summary` | Output summary |
| `error_message` | Error message when failed |
| `created_at` | Created time |

## `trace_span`

| Field | Notes |
| --- | --- |
| `id` | Primary key |
| `trace_id` | Related trace ID |
| `span_id` | Unique span ID |
| `parent_span_id` | Parent span ID, nullable |
| `span_type` | `AGENT`, `MODEL`, `TOOL`, `SYSTEM` |
| `name` | Span name |
| `status` | `RUNNING`, `SUCCESS`, `FAILED` |
| `start_time` | Span start time |
| `end_time` | Span end time |
| `duration_ms` | Span duration |
| `input_payload` | JSON input payload |
| `output_payload` | JSON output payload |
| `error_message` | Error message when failed |

## `model_call_record`

| Field | Notes |
| --- | --- |
| `id` | Primary key |
| `trace_id` | Related trace ID |
| `span_id` | Related span ID |
| `provider` | Model provider |
| `model_name` | Model name |
| `prompt_tokens` | Input token count |
| `completion_tokens` | Output token count |
| `total_tokens` | Total token count |
| `cost` | Estimated cost |
| `latency_ms` | Model latency |
| `status` | `SUCCESS`, `FAILED` |
| `error_message` | Error message when failed |
| `created_at` | Created time |

## `tool_call_record`

| Field | Notes |
| --- | --- |
| `id` | Primary key |
| `trace_id` | Related trace ID |
| `span_id` | Related span ID |
| `tool_name` | Tool name |
| `tool_type` | Tool type |
| `request_payload` | JSON request payload |
| `response_payload` | JSON response payload |
| `latency_ms` | Tool latency |
| `status` | `SUCCESS`, `FAILED` |
| `error_message` | Error message when failed |
| `created_at` | Created time |

## `error_record`

| Field | Notes |
| --- | --- |
| `id` | Primary key |
| `trace_id` | Related trace ID |
| `span_id` | Related span ID, nullable |
| `error_type` | Exception type |
| `error_message` | Error message |
| `stack_summary` | Short stack trace summary |
| `resolved` | Whether the error has been handled |
| `created_at` | Created time |

## Recommended Indexes

- `trace_app.app_key`
- `trace_app.owner_id`
- `trace_record.trace_id`
- `trace_record.app_id, start_time`
- `trace_record.status, start_time`
- `trace_record.duration_ms`
- `trace_span.trace_id`
- `trace_span.parent_span_id`
- `model_call_record.trace_id`
- `model_call_record.span_id`
- `tool_call_record.trace_id`
- `tool_call_record.span_id`
- `error_record.trace_id`
- `error_record.created_at`

