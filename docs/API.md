# AgentTrace API Design

## API Groups

- Auth APIs use JWT after login.
- App management APIs use JWT.
- Trace query and dashboard APIs use JWT.
- SDK ingestion APIs use `appKey` and `apiKey`.

## Response Shape

Use one shared response wrapper for user-facing and SDK APIs:

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

Paged APIs should return:

```json
{
  "records": [],
  "page": 1,
  "pageSize": 20,
  "total": 0
}
```

## Auth APIs

```text
POST /api/auth/register
POST /api/auth/login
GET  /api/auth/me
```

### Login Response

Return JWT token and basic user information.

## Application APIs

```text
POST   /api/apps
GET    /api/apps
GET    /api/apps/{id}
PUT    /api/apps/{id}
POST   /api/apps/{id}/rotate-key
PATCH  /api/apps/{id}/status
```

Rules:

- Developers can manage their own applications.
- Viewers can only read applications they are allowed to see.
- Admin users can manage all applications.
- API key raw value is only returned when created or rotated.

## SDK Ingestion APIs

```text
POST /api/ingest/traces/start
POST /api/ingest/traces/{traceId}/finish
POST /api/ingest/spans/start
POST /api/ingest/spans/{spanId}/finish
POST /api/ingest/model-calls
POST /api/ingest/tool-calls
POST /api/ingest/errors
```

Authentication:

- Request includes `appKey`.
- Request includes `apiKey`.
- Server hashes and verifies `apiKey` against `trace_app.api_key_hash`.
- Disabled applications cannot ingest events.

Idempotency:

- Starting the same `trace_id` twice should not create duplicate records.
- Starting the same `span_id` twice should not create duplicate records.
- Finishing an already finished trace or span should return a clear success or conflict result according to implementation choice.

## Trace Query APIs

```text
GET /api/traces
GET /api/traces/{traceId}
GET /api/traces/{traceId}/spans
GET /api/traces/{traceId}/model-calls
GET /api/traces/{traceId}/tool-calls
GET /api/traces/{traceId}/errors
```

Trace list filters:

- `appId`
- `status`
- `startTime`
- `endTime`
- `minDurationMs`
- `keyword`
- `page`
- `pageSize`

Trace detail should support rendering:

- Basic trace information
- Span tree
- Timeline data
- Model calls
- Tool calls
- Errors

## Dashboard APIs

```text
GET /api/dashboard/overview
GET /api/dashboard/trend
GET /api/dashboard/slow-traces
GET /api/dashboard/error-ranking
GET /api/dashboard/model-cost
```

Dashboard filters:

- `appId`
- `startTime`
- `endTime`

Overview metrics:

- Trace count
- Success rate
- Failure rate
- Average duration
- Total token count
- Average token count
- Estimated model cost

