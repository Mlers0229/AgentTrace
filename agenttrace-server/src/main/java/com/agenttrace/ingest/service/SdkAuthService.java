package com.agenttrace.ingest.service;

import com.agenttrace.app.entity.TraceApp;
import com.agenttrace.app.mapper.TraceAppMapper;
import com.agenttrace.ingest.dto.SdkCredentialRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SdkAuthService {

    private final TraceAppMapper appMapper;
    private final PasswordEncoder passwordEncoder;

    public SdkAuthService(TraceAppMapper appMapper, PasswordEncoder passwordEncoder) {
        this.appMapper = appMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public TraceApp authenticate(SdkCredentialRequest request) {
        TraceApp app = appMapper.selectOne(new LambdaQueryWrapper<TraceApp>()
                .eq(TraceApp::getAppKey, request.appKey()));
        if (app == null || !passwordEncoder.matches(request.apiKey(), app.getApiKeyHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid app credentials");
        }
        if (!"ENABLED".equals(app.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "application is disabled");
        }
        return app;
    }
}

