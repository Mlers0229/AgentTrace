package com.agenttrace.app.service;

import com.agenttrace.app.dto.AppResponse;
import com.agenttrace.app.dto.CreateAppRequest;
import com.agenttrace.app.dto.CreateAppResponse;
import com.agenttrace.app.dto.RotateKeyResponse;
import com.agenttrace.app.dto.UpdateAppRequest;
import com.agenttrace.app.dto.UpdateAppStatusRequest;
import com.agenttrace.app.entity.TraceApp;
import com.agenttrace.app.mapper.TraceAppMapper;
import com.agenttrace.auth.security.UserPrincipal;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationService {

    private final TraceAppMapper appMapper;
    private final ApiKeyGenerator keyGenerator;
    private final PasswordEncoder passwordEncoder;

    public ApplicationService(TraceAppMapper appMapper, ApiKeyGenerator keyGenerator, PasswordEncoder passwordEncoder) {
        this.appMapper = appMapper;
        this.keyGenerator = keyGenerator;
        this.passwordEncoder = passwordEncoder;
    }

    public CreateAppResponse create(CreateAppRequest request, UserPrincipal principal) {
        LocalDateTime now = LocalDateTime.now();
        String apiKey = keyGenerator.generateApiKey();
        TraceApp app = new TraceApp();
        app.setAppKey(keyGenerator.generateAppKey());
        app.setAppName(request.appName());
        app.setDescription(request.description());
        app.setEnvironment(request.environment());
        app.setApiKeyHash(passwordEncoder.encode(apiKey));
        app.setOwnerId(principal.id());
        app.setStatus("ENABLED");
        app.setCreatedAt(now);
        app.setUpdatedAt(now);
        appMapper.insert(app);
        return new CreateAppResponse(toResponse(app), apiKey);
    }

    public List<AppResponse> list(UserPrincipal principal) {
        LambdaQueryWrapper<TraceApp> query = new LambdaQueryWrapper<TraceApp>()
                .orderByDesc(TraceApp::getCreatedAt);
        if (!principal.isAdmin()) {
            query.eq(TraceApp::getOwnerId, principal.id());
        }
        return appMapper.selectList(query).stream()
                .map(this::toResponse)
                .toList();
    }

    public AppResponse detail(Long id, UserPrincipal principal) {
        return toResponse(requireAccessible(id, principal));
    }

    public AppResponse update(Long id, UpdateAppRequest request, UserPrincipal principal) {
        TraceApp app = requireAccessible(id, principal);
        app.setAppName(request.appName());
        app.setDescription(request.description());
        app.setEnvironment(request.environment());
        app.setUpdatedAt(LocalDateTime.now());
        appMapper.updateById(app);
        return toResponse(app);
    }

    public RotateKeyResponse rotateKey(Long id, UserPrincipal principal) {
        TraceApp app = requireAccessible(id, principal);
        String apiKey = keyGenerator.generateApiKey();
        app.setApiKeyHash(passwordEncoder.encode(apiKey));
        app.setUpdatedAt(LocalDateTime.now());
        appMapper.updateById(app);
        return new RotateKeyResponse(app.getId(), app.getAppKey(), apiKey);
    }

    public AppResponse updateStatus(Long id, UpdateAppStatusRequest request, UserPrincipal principal) {
        TraceApp app = requireAccessible(id, principal);
        if (!"ENABLED".equals(request.status()) && !"DISABLED".equals(request.status())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status must be ENABLED or DISABLED");
        }
        app.setStatus(request.status());
        app.setUpdatedAt(LocalDateTime.now());
        appMapper.updateById(app);
        return toResponse(app);
    }

    private TraceApp requireAccessible(Long id, UserPrincipal principal) {
        TraceApp app = appMapper.selectById(id);
        if (app == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "application not found");
        }
        if (!principal.isAdmin() && !app.getOwnerId().equals(principal.id())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "application access denied");
        }
        return app;
    }

    private AppResponse toResponse(TraceApp app) {
        return new AppResponse(
                app.getId(),
                app.getAppKey(),
                app.getAppName(),
                app.getDescription(),
                app.getEnvironment(),
                app.getOwnerId(),
                app.getStatus(),
                app.getCreatedAt(),
                app.getUpdatedAt()
        );
    }
}

