create table if not exists sys_user (
    id bigserial primary key,
    username varchar(64) not null unique,
    password_hash varchar(255) not null,
    email varchar(128),
    role varchar(32) not null,
    status varchar(32) not null default 'ENABLED',
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);

create table if not exists trace_app (
    id bigserial primary key,
    app_key varchar(64) not null unique,
    app_name varchar(128) not null,
    description varchar(500),
    environment varchar(32),
    api_key_hash varchar(255) not null,
    owner_id bigint not null,
    status varchar(32) not null default 'ENABLED',
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);

create table if not exists trace_record (
    id bigserial primary key,
    trace_id varchar(64) not null unique,
    app_id bigint not null,
    name varchar(255) not null,
    status varchar(32) not null,
    start_time timestamp not null,
    end_time timestamp,
    duration_ms bigint,
    input_summary text,
    output_summary text,
    error_message text,
    created_at timestamp not null default now()
);

create table if not exists trace_span (
    id bigserial primary key,
    trace_id varchar(64) not null,
    span_id varchar(64) not null unique,
    parent_span_id varchar(64),
    span_type varchar(32) not null,
    name varchar(255) not null,
    status varchar(32) not null,
    start_time timestamp not null,
    end_time timestamp,
    duration_ms bigint,
    input_payload jsonb,
    output_payload jsonb,
    error_message text
);

create table if not exists model_call_record (
    id bigserial primary key,
    trace_id varchar(64) not null,
    span_id varchar(64) not null,
    provider varchar(64) not null,
    model_name varchar(128) not null,
    prompt_tokens int not null default 0,
    completion_tokens int not null default 0,
    total_tokens int not null default 0,
    cost numeric(18,6) not null default 0,
    latency_ms bigint not null default 0,
    status varchar(32) not null,
    error_message text,
    created_at timestamp not null default now()
);

create table if not exists tool_call_record (
    id bigserial primary key,
    trace_id varchar(64) not null,
    span_id varchar(64) not null,
    tool_name varchar(128) not null,
    tool_type varchar(64) not null,
    request_payload jsonb,
    response_payload jsonb,
    latency_ms bigint not null default 0,
    status varchar(32) not null,
    error_message text,
    created_at timestamp not null default now()
);

create table if not exists error_record (
    id bigserial primary key,
    trace_id varchar(64) not null,
    span_id varchar(64),
    error_type varchar(128) not null,
    error_message text not null,
    stack_summary text,
    resolved boolean not null default false,
    created_at timestamp not null default now()
);

create index if not exists idx_trace_record_app_start on trace_record (app_id, start_time desc);
create index if not exists idx_trace_record_status_start on trace_record (status, start_time desc);
create index if not exists idx_trace_record_duration on trace_record (duration_ms desc);
create index if not exists idx_trace_span_trace on trace_span (trace_id);
create index if not exists idx_trace_span_parent on trace_span (parent_span_id);
create index if not exists idx_model_call_trace on model_call_record (trace_id);
create index if not exists idx_model_call_span on model_call_record (span_id);
create index if not exists idx_tool_call_trace on tool_call_record (trace_id);
create index if not exists idx_tool_call_span on tool_call_record (span_id);
create index if not exists idx_error_record_trace on error_record (trace_id);
create index if not exists idx_error_record_created_at on error_record (created_at desc);

