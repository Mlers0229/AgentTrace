package com.agenttrace.auth.service;

import com.agenttrace.auth.dto.AuthTokenResponse;
import com.agenttrace.auth.dto.LoginRequest;
import com.agenttrace.auth.dto.RegisterRequest;
import com.agenttrace.auth.dto.UserResponse;
import com.agenttrace.auth.entity.SysUser;
import com.agenttrace.auth.mapper.SysUserMapper;
import com.agenttrace.auth.security.JwtTokenProvider;
import com.agenttrace.auth.security.UserPrincipal;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(SysUserMapper userMapper, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public UserResponse register(RegisterRequest request) {
        SysUser existing = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.username()));
        if (existing != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "username already exists");
        }

        LocalDateTime now = LocalDateTime.now();
        SysUser user = new SysUser();
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setRole("DEVELOPER");
        user.setStatus("ENABLED");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        userMapper.insert(user);
        return toResponse(user);
    }

    public AuthTokenResponse login(LoginRequest request) {
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.username()));
        if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid username or password");
        }
        if (!"ENABLED".equals(user.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "user is disabled");
        }
        return new AuthTokenResponse(
                "Bearer",
                tokenProvider.generate(user),
                tokenProvider.expiresInSeconds(),
                toResponse(user)
        );
    }

    public UserResponse currentUser(UserPrincipal principal) {
        SysUser user = userMapper.selectById(principal.id());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user not found");
        }
        return toResponse(user);
    }

    private UserResponse toResponse(SysUser user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.getStatus());
    }
}

