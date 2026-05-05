package com.agenttrace.auth.security;

public record UserPrincipal(
        Long id,
        String username,
        String role,
        String status
) {
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}

