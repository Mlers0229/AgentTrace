# AGENTS.md

This file defines the working rules for AI agents developing AgentTrace.

## Project Positioning

AgentTrace is an observability platform for AI Agent applications. The MVP must complete the full loop:

```text
Application creation -> SDK integration -> Trace ingestion -> Database storage -> Query pages -> Dashboard statistics
```

Do not turn the project into a generic chat app, generic log system, or unrelated admin template.

## Current Technical Decisions

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

The persistence layer is fixed as MyBatis Plus. Do not introduce Spring Data JPA.

### Frontend

- Vue 3
- TypeScript
- Vite
- Pinia
- Vue Router
- Naive UI or Element Plus
- ECharts

## Backend Conventions

- Use layered Spring Boot architecture: controller, service, mapper, entity, dto, vo, config, common.
- Use MyBatis Plus `BaseMapper` and `IService` / `ServiceImpl` for common CRUD.
- Use custom Mapper XML or annotation SQL for complex Trace detail queries and dashboard statistics.
- Use MyBatis Plus pagination plugin for paged APIs.
- Use `LocalDateTime` for timestamp fields.
- Use `BigDecimal` for cost fields.
- Store JSON payload fields as `String` in Java first, backed by PostgreSQL `jsonb`.
- Manage database migrations with Flyway in the first version.
- Keep API responses consistent through a shared response wrapper.
- Keep authentication and SDK ingestion authentication separate:
  - User-facing APIs use JWT.
  - SDK ingestion APIs use `appKey` and `apiKey`.

## Domain Rules

- `trace_id` identifies one full request execution.
- `span_id` identifies one execution node inside a trace.
- `parent_span_id` models the parent-child relationship between spans.
- Trace status values: `RUNNING`, `SUCCESS`, `FAILED`.
- Span type values: `AGENT`, `MODEL`, `TOOL`, `SYSTEM`.
- Slow traces should be queryable by duration threshold.
- Model calls and tool calls are linked to both `trace_id` and `span_id`.
- Errors are linked to `trace_id`, and linked to `span_id` when available.

## Development Order

1. Scaffold backend project.
2. Add PostgreSQL, Redis, MyBatis Plus, Flyway, Swagger, and security dependencies.
3. Create base tables and entity mappings.
4. Implement auth and app management.
5. Implement SDK ingestion APIs.
6. Implement Trace query APIs.
7. Implement dashboard statistics.
8. Add Java SDK.
9. Add frontend dashboard.
10. Add Docker Compose and demo data.

## Documentation Map

- `agent.md`: original full product blueprint.
- `docs/ARCHITECTURE.md`: system architecture and module boundaries.
- `docs/DATABASE.md`: database model and table responsibilities.
- `docs/API.md`: API surface and authentication model.
- `docs/DEV_PLAN.md`: staged implementation plan.

## Quality Bar

- Keep the MVP small but end-to-end.
- Prefer concrete implementation over speculative abstractions.
- Add tests around authentication, app access control, trace ingestion, and dashboard statistics.
- Keep generated demo data realistic enough to support screenshots and interview explanation.

