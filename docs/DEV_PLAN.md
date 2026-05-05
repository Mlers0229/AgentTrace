# AgentTrace Development Plan

## Phase 1: Backend Foundation

Goal: create a runnable Spring Boot backend with database migrations and basic security structure.

Tasks:

- Scaffold Spring Boot 3 project.
- Configure Java 17+.
- Add MyBatis Plus, PostgreSQL driver, Flyway, Spring Security, JWT, validation, Swagger, Redis, and test dependencies.
- Add Docker Compose for PostgreSQL and Redis.
- Create base package structure.
- Add common response wrapper and global exception handler.
- Add Flyway migration for core tables.
- Configure MyBatis Plus pagination.

Exit criteria:

- Backend starts successfully.
- Database migrations run successfully.
- Swagger page is reachable.
- A simple health endpoint works.

## Phase 2: Auth And Application Management

Goal: support platform users and SDK application credentials.

Tasks:

- Implement user registration.
- Implement login and JWT issuing.
- Implement current-user API.
- Implement role model: `ADMIN`, `DEVELOPER`, `VIEWER`.
- Implement application create, update, list, detail, status switch, and key rotation.
- Store only API key hash.

Exit criteria:

- A developer can register, login, create an application, and obtain an SDK API key.
- JWT protected APIs reject unauthenticated requests.

## Phase 3: Trace Ingestion

Goal: allow external AI Agent applications to report trace events.

Tasks:

- Implement SDK credential authentication.
- Implement start and finish Trace APIs.
- Implement start and finish Span APIs.
- Implement model call record API.
- Implement tool call record API.
- Implement error record API.
- Add idempotency checks for trace and span IDs.

Exit criteria:

- A simulated client can create a trace, add spans, record model/tool calls, record errors, and finish the trace.

## Phase 4: Trace Query

Goal: allow developers to inspect traces and related records.

Tasks:

- Implement trace list with filters and pagination.
- Implement trace detail API.
- Implement span tree query.
- Implement model call, tool call, and error list APIs by trace.
- Add slow trace filtering.

Exit criteria:

- A failed or slow trace can be found from the list and inspected in detail.

## Phase 5: Dashboard

Goal: expose core observability metrics.

Tasks:

- Implement overview statistics.
- Implement trend statistics.
- Implement slow trace Top 10.
- Implement error ranking.
- Implement model cost statistics.

Exit criteria:

- Dashboard APIs can support the planned frontend overview page.

## Phase 6: Java SDK

Goal: provide lightweight integration for Java Agent applications.

Tasks:

- Create `agenttrace-sdk-java` module.
- Implement `AgentTraceClient`.
- Implement `TraceContext` and `SpanContext`.
- Implement request DTOs.
- Implement HTTP transport.
- Provide a demo or test client.

Exit criteria:

- SDK sample can report a full trace to local AgentTrace server.

## Phase 7: Frontend Dashboard

Goal: build a usable web interface for the MVP workflow.

Tasks:

- Scaffold Vue 3 + TypeScript + Vite project.
- Add router, state management, UI library, and chart library.
- Implement login page.
- Implement application management page.
- Implement trace list page.
- Implement trace detail page.
- Implement dashboard page.

Exit criteria:

- A user can login, manage apps, view traces, inspect details, and read dashboard metrics.

## Phase 8: Deployment And Portfolio Material

Goal: make the project demonstrable.

Tasks:

- Add production Dockerfiles.
- Add Docker Compose for backend, frontend, PostgreSQL, Redis, and Nginx.
- Add demo data.
- Add README setup instructions.
- Prepare screenshots.
- Prepare resume project description.

Exit criteria:

- The project can be started from Docker Compose.
- README explains local startup, API docs, and demo credentials.

