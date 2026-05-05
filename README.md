# AgentTrace

AgentTrace is an observability platform for AI Agent applications. It records and analyzes the full execution path of a user request across agents, tool calls, model calls, spans, errors, token usage, latency, and estimated cost.

## Project Goal

Build a trace-based debugging and analysis platform for multi-agent AI applications:

- Trace user requests from start to finish.
- Record Agent, model, tool, and system spans.
- Locate failed or slow execution steps.
- Analyze token usage, latency, success rate, and estimated model cost.
- Provide a Java SDK for lightweight event reporting.

## Planned Stack

### Backend

- Java 17+
- Spring Boot 3
- Spring Security
- JWT
- MyBatis Plus
- PostgreSQL
- Redis
- Knife4j / Swagger
- JUnit
- Docker

### Frontend

- Vue 3
- TypeScript
- Vite
- Pinia
- Vue Router
- Naive UI or Element Plus
- ECharts

## Documentation

The current development blueprint is maintained in [agent.md](./agent.md).

Additional engineering documents:

- [AGENTS.md](./AGENTS.md): AI agent collaboration rules and technical constraints.
- [docs/ARCHITECTURE.md](./docs/ARCHITECTURE.md): architecture and module boundaries.
- [docs/DATABASE.md](./docs/DATABASE.md): database model and table responsibilities.
- [docs/API.md](./docs/API.md): API groups and authentication model.
- [docs/DEV_PLAN.md](./docs/DEV_PLAN.md): staged implementation plan.

## Local Backend Startup

Start PostgreSQL and Redis:

```powershell
docker compose up -d postgres redis
```

Compile the backend:

```powershell
mvn -q -DskipTests compile
```

Run the API server:

```powershell
mvn -pl agenttrace-server spring-boot:run
```

Health check:

```text
GET http://localhost:8080/api/health
```

Current backend API groups:

```text
POST   /api/auth/register
POST   /api/auth/login
GET    /api/auth/me
POST   /api/apps
GET    /api/apps
GET    /api/apps/{id}
PUT    /api/apps/{id}
POST   /api/apps/{id}/rotate-key
PATCH  /api/apps/{id}/status
POST   /api/ingest/traces/start
POST   /api/ingest/traces/{traceId}/finish
POST   /api/ingest/spans/start
POST   /api/ingest/spans/{spanId}/finish
POST   /api/ingest/model-calls
POST   /api/ingest/tool-calls
POST   /api/ingest/errors
```

SDK ingestion APIs use `appKey` and `apiKey` in the request body. Minimal flow:

```json
{
  "appKey": "app_xxx",
  "apiKey": "atk_xxx",
  "traceId": "trace-demo-001",
  "name": "generate-learning-plan"
}
```
