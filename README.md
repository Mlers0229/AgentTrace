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

