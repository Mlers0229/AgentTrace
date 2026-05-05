# AgentTrace 多 Agent 调用链路观测平台

## 1. 项目定位

AgentTrace 是一个面向 AI Agent 应用的可观测性平台，用于记录、追踪和分析一次用户请求在多个 Agent、工具调用和模型调用之间的完整执行链路。

项目目标不是再做一个普通 AI 聊天应用，而是解决 AI Agent 应用开发中的真实工程问题：

- 一次请求经过了哪些 Agent？
- 每个 Agent 做了什么决策？
- 调用了哪些工具和模型？
- 哪一步失败了？
- 哪一步耗时最高？
- 每次请求消耗了多少 Token 和成本？
- 如何回放一次 Agent 执行过程，方便调试和优化？

一句话介绍：

> 面向 AI Agent 应用的调用链路观测平台，通过 Trace ID 串联用户请求、Agent 节点、工具调用与模型响应，支持执行过程回放、异常定位、Token 成本统计和慢调用分析。

## 2. 项目价值

### 2.1 对求职简历的价值

这个项目适合作为第二个核心项目，用来替换普通工具平台或弱原型项目。

它和 LearnFlow 的关系是：

- LearnFlow：证明具备 AI 应用开发和全栈交付能力。
- AgentTrace：证明具备 AI 工程化、日志治理、链路追踪、后端建模和问题排查能力。

组合后的简历表达会更完整：

> 我不只是会开发 AI Agent 应用，还能建设 Agent 应用的调用链路追踪、日志治理、异常定位和成本分析能力。

### 2.2 对后端能力的体现

AgentTrace 可以体现以下后端能力：

- Spring Boot 分层架构设计
- RESTful API 设计
- 认证授权与应用接入管理
- Trace / Span 链路模型设计
- 日志采集、存储与查询
- PostgreSQL 数据建模
- Redis 缓存和统计
- 异常聚合与慢调用分析
- Dashboard 数据统计
- SDK 设计与外部系统接入
- Docker / Nginx / Linux 部署

## 3. 用户角色

### 3.1 平台管理员

负责管理用户、应用、接入密钥、系统配置和全局数据。

### 3.2 应用开发者

负责接入自己的 AI Agent 应用，查看 Trace 链路、调用日志、异常和成本统计。

### 3.3 观察者

只能查看应用的调用数据和统计看板，不能修改应用配置。

## 4. 核心业务流程

### 4.1 应用接入流程

1. 开发者在平台创建一个应用。
2. 平台生成 `appId` 和 `apiKey`。
3. 开发者在 AI Agent 应用中引入 AgentTrace SDK。
4. SDK 在请求开始时创建 Trace。
5. SDK 在 Agent 执行、工具调用、模型调用和异常发生时上报事件。
6. 平台接收事件并持久化。
7. 开发者在平台查看完整执行链路。

### 4.2 Trace 采集流程

```text
用户请求
  -> 创建 Trace
  -> GoalAgent 执行
  -> PlanAgent 执行
  -> Tool 调用
  -> Model 调用
  -> TutorAgent 执行
  -> Trace 结束
```

每个步骤都记录为 Span 或事件，最终形成一条完整的调用链。

### 4.3 问题排查流程

1. 开发者进入 Trace 列表。
2. 按应用、状态、耗时、时间范围筛选。
3. 找到失败或慢调用 Trace。
4. 查看 Trace 详情页。
5. 定位失败节点、异常信息、请求参数和模型响应。
6. 根据 Token、耗时和错误信息优化 Agent 流程。

## 5. MVP 功能范围

第一版只做足够能展示项目价值和后端深度的功能。

### 5.1 用户与权限

- 用户注册和登录
- JWT 认证
- 用户角色：管理员、开发者、观察者
- 应用级访问控制

### 5.2 应用管理

- 创建应用
- 编辑应用名称、描述和环境
- 生成应用接入密钥
- 禁用和启用应用
- 查看应用接入状态

### 5.3 Trace 链路追踪

- 创建 Trace
- 结束 Trace
- Trace 列表查询
- Trace 详情查看
- Trace 状态筛选：成功、失败、执行中
- 慢 Trace 筛选

### 5.4 Span 节点记录

- 记录 Agent 执行节点
- 记录工具调用节点
- 记录模型调用节点
- 记录节点开始时间、结束时间和耗时
- 记录输入摘要和输出摘要
- 支持父子节点关系

### 5.5 模型调用记录

- 模型名称
- Provider
- Prompt Token
- Completion Token
- Total Token
- 调用耗时
- 调用状态
- 错误信息
- 预估调用成本

### 5.6 工具调用记录

- 工具名称
- 工具类型
- 请求参数
- 返回结果摘要
- 调用状态
- 调用耗时
- 异常信息

### 5.7 异常记录

- 异常类型
- 异常消息
- 堆栈摘要
- 关联 Trace
- 关联 Span
- 发生时间
- 是否已处理

### 5.8 Dashboard 看板

- 今日 Trace 数量
- 成功率
- 失败率
- 平均耗时
- 平均 Token 消耗
- 总 Token 消耗
- 预估调用成本
- 慢调用 Top 10
- 失败 Trace Top 10

### 5.9 Java SDK

第一版 SDK 可以保持轻量，只提供最核心的上报能力。

示例：

```java
AgentTraceClient client = AgentTraceClient.builder()
    .endpoint("http://localhost:8080")
    .appId("learnflow")
    .apiKey("your-api-key")
    .build();

TraceContext trace = client.startTrace("generate-learning-plan");

SpanContext span = client.startSpan(trace, "PlanAgent", SpanType.AGENT);
client.recordModelCall(span, request, response);
client.finishSpan(span);

client.finishTrace(trace);
```

SDK 核心能力：

- 创建 Trace
- 创建 Span
- 记录模型调用
- 记录工具调用
- 记录异常
- 结束 Span
- 结束 Trace

## 6. 非 MVP 功能

这些功能可以作为后续扩展，不放入第一版。

- OpenTelemetry 协议兼容
- Agent 执行过程可视化回放
- 实时 WebSocket 日志推送
- 告警规则配置
- 邮件 / 企业微信 / 飞书通知
- 多租户
- 调用链路拓扑图
- AI 自动分析失败原因
- AI 自动生成优化建议
- 和 LearnFlow 深度集成

## 7. 技术栈

### 7.1 后端

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

后端持久层确定使用 MyBatis Plus，不使用 Spring Data JPA。后续代码实现按以下约定推进：

- 实体类与数据库表保持清晰映射，使用 `@TableName`、`@TableId`、`@TableField` 标注关键字段。
- 通用 CRUD 优先使用 MyBatis Plus 的 `BaseMapper` 和 `IService` / `ServiceImpl`。
- 复杂查询、统计看板、Trace 详情聚合查询使用自定义 Mapper XML 或注解 SQL。
- 分页查询使用 MyBatis Plus 分页插件。
- 时间字段统一使用 `LocalDateTime`。
- 金额与成本字段使用 `BigDecimal`。
- JSON 内容字段先以 `String` 保存，数据库使用 PostgreSQL `jsonb`，后续按需要增加 TypeHandler。
- 数据库变更脚本建议使用 Flyway 或 Liquibase 管理，第一版优先使用 Flyway。

### 7.2 前端

- Vue 3
- TypeScript
- Vite
- Pinia
- Vue Router
- Naive UI 或 Element Plus
- ECharts

### 7.3 部署

- Linux
- Nginx
- Docker Compose
- systemd

## 8. 系统架构

```text
AI Agent 应用
  -> AgentTrace Java SDK
  -> AgentTrace API Server
  -> PostgreSQL
  -> Redis
  -> Web Dashboard
```

模块划分：

```text
agenttrace-server
  auth          登录认证与权限
  app           应用管理
  trace         Trace 管理
  span          Span 管理
  model         模型调用记录
  tool          工具调用记录
  error         异常记录
  dashboard     统计看板
  ingest        SDK 数据上报入口

agenttrace-sdk-java
  client        上报客户端
  context       TraceContext / SpanContext
  model         上报请求模型
  transport     HTTP 传输
```

## 9. 数据模型设计

### 9.1 用户表 `sys_user`

| 字段 | 说明 |
| --- | --- |
| id | 用户 ID |
| username | 用户名 |
| password_hash | 密码哈希 |
| email | 邮箱 |
| role | 角色 |
| status | 状态 |
| created_at | 创建时间 |
| updated_at | 更新时间 |

### 9.2 应用表 `trace_app`

| 字段 | 说明 |
| --- | --- |
| id | 应用 ID |
| app_key | 应用唯一标识 |
| app_name | 应用名称 |
| description | 应用描述 |
| api_key_hash | 接入密钥哈希 |
| owner_id | 所属用户 |
| status | 状态 |
| created_at | 创建时间 |
| updated_at | 更新时间 |

### 9.3 Trace 表 `trace_record`

| 字段 | 说明 |
| --- | --- |
| id | 主键 |
| trace_id | 链路 ID |
| app_id | 应用 ID |
| name | Trace 名称 |
| status | 状态：RUNNING / SUCCESS / FAILED |
| start_time | 开始时间 |
| end_time | 结束时间 |
| duration_ms | 总耗时 |
| input_summary | 输入摘要 |
| output_summary | 输出摘要 |
| error_message | 错误信息 |
| created_at | 创建时间 |

### 9.4 Span 表 `trace_span`

| 字段 | 说明 |
| --- | --- |
| id | 主键 |
| trace_id | 链路 ID |
| span_id | 节点 ID |
| parent_span_id | 父节点 ID |
| span_type | AGENT / MODEL / TOOL / SYSTEM |
| name | 节点名称 |
| status | 状态 |
| start_time | 开始时间 |
| end_time | 结束时间 |
| duration_ms | 耗时 |
| input_payload | 输入内容 JSON |
| output_payload | 输出内容 JSON |
| error_message | 错误信息 |

### 9.5 模型调用表 `model_call_record`

| 字段 | 说明 |
| --- | --- |
| id | 主键 |
| trace_id | 链路 ID |
| span_id | 节点 ID |
| provider | 模型服务商 |
| model_name | 模型名称 |
| prompt_tokens | 输入 Token |
| completion_tokens | 输出 Token |
| total_tokens | 总 Token |
| cost | 预估成本 |
| latency_ms | 调用耗时 |
| status | 状态 |
| error_message | 错误信息 |
| created_at | 创建时间 |

### 9.6 工具调用表 `tool_call_record`

| 字段 | 说明 |
| --- | --- |
| id | 主键 |
| trace_id | 链路 ID |
| span_id | 节点 ID |
| tool_name | 工具名称 |
| tool_type | 工具类型 |
| request_payload | 请求参数 JSON |
| response_payload | 响应摘要 JSON |
| latency_ms | 调用耗时 |
| status | 状态 |
| error_message | 错误信息 |
| created_at | 创建时间 |

### 9.7 异常表 `error_record`

| 字段 | 说明 |
| --- | --- |
| id | 主键 |
| trace_id | 链路 ID |
| span_id | 节点 ID |
| error_type | 异常类型 |
| error_message | 异常消息 |
| stack_summary | 堆栈摘要 |
| resolved | 是否已处理 |
| created_at | 创建时间 |

## 10. API 设计

### 10.1 认证接口

```text
POST /api/auth/register
POST /api/auth/login
GET  /api/auth/me
```

### 10.2 应用管理接口

```text
POST   /api/apps
GET    /api/apps
GET    /api/apps/{id}
PUT    /api/apps/{id}
POST   /api/apps/{id}/rotate-key
PATCH  /api/apps/{id}/status
```

### 10.3 SDK 上报接口

```text
POST /api/ingest/traces/start
POST /api/ingest/traces/{traceId}/finish
POST /api/ingest/spans/start
POST /api/ingest/spans/{spanId}/finish
POST /api/ingest/model-calls
POST /api/ingest/tool-calls
POST /api/ingest/errors
```

### 10.4 Trace 查询接口

```text
GET /api/traces
GET /api/traces/{traceId}
GET /api/traces/{traceId}/spans
GET /api/traces/{traceId}/model-calls
GET /api/traces/{traceId}/tool-calls
GET /api/traces/{traceId}/errors
```

### 10.5 Dashboard 接口

```text
GET /api/dashboard/overview
GET /api/dashboard/trend
GET /api/dashboard/slow-traces
GET /api/dashboard/error-ranking
GET /api/dashboard/model-cost
```

## 11. 前端页面设计

### 11.1 登录页

- 登录表单
- 注册入口
- Token 保存

### 11.2 应用管理页

- 应用列表
- 创建应用
- 查看 appKey
- 重置 apiKey
- 启用 / 禁用应用

### 11.3 Dashboard 首页

- Trace 总数
- 成功率
- 平均耗时
- Token 总消耗
- 成本统计
- 慢调用排行
- 错误排行
- 调用趋势图

### 11.4 Trace 列表页

- 按应用筛选
- 按状态筛选
- 按时间筛选
- 按耗时排序
- 查看 Trace 详情

### 11.5 Trace 详情页

- 基本信息
- 执行时间线
- Span 树
- 模型调用记录
- 工具调用记录
- 异常记录
- 输入输出摘要

### 11.6 异常分析页

- 异常列表
- 异常类型统计
- 关联 Trace
- 标记已处理

## 12. 开发阶段规划

### 第一阶段：后端基础能力

- 初始化 Spring Boot 项目
- 配置 PostgreSQL
- 实现用户登录和 JWT
- 实现应用管理
- 完成基础数据表

### 第二阶段：Trace 采集与查询

- 实现 Trace 创建和结束
- 实现 Span 创建和结束
- 实现模型调用记录
- 实现工具调用记录
- 实现异常记录
- 实现 Trace 详情查询

### 第三阶段：Java SDK

- 创建 SDK 模块
- 封装 HTTP 上报客户端
- 封装 TraceContext 和 SpanContext
- 提供示例 Demo
- 接入 LearnFlow 或模拟 Agent 应用

### 第四阶段：前端看板

- 实现登录页
- 实现应用管理页
- 实现 Trace 列表页
- 实现 Trace 详情页
- 实现 Dashboard 统计页

### 第五阶段：部署与简历材料

- Docker Compose 部署
- Nginx 反向代理
- 编写 README
- 准备截图
- 准备演示数据
- 准备简历项目描述

## 13. 可量化指标

开发完成后，简历中可以使用真实统计数据：

- 设计 7+ 张核心数据表
- 实现 25+ 个 REST API
- 支持 Agent、模型、工具、异常 4 类事件上报
- 支持 Trace / Span 父子链路追踪
- 支持 Token、耗时、失败率和成本统计
- 接入 1 个真实 AI Agent 项目或 1 个模拟 Agent Demo
- 完成 Docker Compose 一键部署

## 14. 简历表达草稿

项目名称：

> AgentTrace 多 Agent 调用链路观测平台

项目简介：

> 面向 AI Agent 应用的可观测性平台，通过 Trace ID 串联用户请求、Agent 节点、工具调用与模型响应，支持执行过程回放、异常定位、Token 成本统计和慢调用分析。

项目描述：

- 独立设计并实现面向 AI Agent 应用的调用链路观测平台，基于 Trace / Span 模型记录用户请求、Agent 执行、工具调用、模型调用和异常信息，形成完整执行链路。
- 抽象 Java SDK 上报模型调用、工具调用、异常与耗时数据，基于 Spring Boot 接收并持久化 Trace 事件，设计 Trace、Span、ModelCall、ToolCall、ErrorLog 等核心数据模型。
- 实现 Trace 列表、链路详情、执行时间线、慢调用排行、失败率统计、Token 消耗和成本看板，支持开发者快速定位 Agent 执行失败、模型响应异常和高耗时节点。
- 在 Linux 环境完成前后端和数据库部署，提供演示数据与接入示例，形成从 SDK 接入、事件采集、数据分析到问题排查的完整闭环。

## 15. 面试讲解重点

### 15.1 为什么做这个项目

可以这样回答：

> 我在做 LearnFlow 这类多 Agent 应用时发现，Agent 的执行过程不像普通接口调用那样直观。一次请求可能经过多个 Agent、模型和工具，如果中间某一步失败，很难快速定位原因。所以我做了 AgentTrace，把每次 Agent 执行过程结构化记录下来，方便回放、统计和排查问题。

### 15.2 项目难点

- 如何设计 Trace / Span 数据模型，表达多 Agent 的父子调用关系。
- 如何让 SDK 接入尽量轻量，不侵入业务代码。
- 如何保证一次链路中多个事件的状态一致性。
- 如何统计 Token、耗时、失败率和成本。
- 如何在前端清晰展示复杂的执行链路。

### 15.3 可以被追问的问题

- Trace ID 是如何生成和传递的？
- Span 的父子关系如何建模？
- SDK 上报失败怎么办？
- 模型调用成本如何估算？
- 大量日志数据如何分页和归档？
- 慢调用如何定义？
- 如何接入已有的 AI Agent 项目？
- 和普通日志系统有什么区别？
- 和 OpenTelemetry 有什么区别？

## 16. 推荐实现边界

第一版要避免过度设计，优先保证闭环完整：

```text
应用创建 -> SDK 接入 -> Trace 上报 -> 数据入库 -> 页面查询 -> Dashboard 统计
```

只要这个闭环跑通，这个项目就已经具备简历价值。

后续再逐步增加：

```text
可视化链路图 -> 告警 -> AI 分析 -> OpenTelemetry 兼容
```
