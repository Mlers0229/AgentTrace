package com.agenttrace.app.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Component
public class ApiKeyGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    public String generateAppKey() {
        return "app_" + UUID.randomUUID().toString().replace("-", "");
    }

    public String generateApiKey() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return "atk_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}

