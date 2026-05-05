package com.agenttrace.app.web;

import com.agenttrace.app.dto.AppResponse;
import com.agenttrace.app.dto.CreateAppRequest;
import com.agenttrace.app.dto.CreateAppResponse;
import com.agenttrace.app.dto.RotateKeyResponse;
import com.agenttrace.app.dto.UpdateAppRequest;
import com.agenttrace.app.dto.UpdateAppStatusRequest;
import com.agenttrace.app.service.ApplicationService;
import com.agenttrace.auth.security.CurrentUser;
import com.agenttrace.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/apps")
public class AppController {

    private final ApplicationService applicationService;

    public AppController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ApiResponse<CreateAppResponse> create(@Valid @RequestBody CreateAppRequest request) {
        return ApiResponse.success(applicationService.create(request, CurrentUser.require()));
    }

    @GetMapping
    public ApiResponse<List<AppResponse>> list() {
        return ApiResponse.success(applicationService.list(CurrentUser.require()));
    }

    @GetMapping("/{id}")
    public ApiResponse<AppResponse> detail(@PathVariable Long id) {
        return ApiResponse.success(applicationService.detail(id, CurrentUser.require()));
    }

    @PutMapping("/{id}")
    public ApiResponse<AppResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateAppRequest request) {
        return ApiResponse.success(applicationService.update(id, request, CurrentUser.require()));
    }

    @PostMapping("/{id}/rotate-key")
    public ApiResponse<RotateKeyResponse> rotateKey(@PathVariable Long id) {
        return ApiResponse.success(applicationService.rotateKey(id, CurrentUser.require()));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<AppResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateAppStatusRequest request) {
        return ApiResponse.success(applicationService.updateStatus(id, request, CurrentUser.require()));
    }
}

