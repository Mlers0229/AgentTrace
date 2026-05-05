package com.agenttrace;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({
        "com.agenttrace.auth.mapper",
        "com.agenttrace.app.mapper"
})
public class AgenttraceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgenttraceServerApplication.class, args);
    }
}
