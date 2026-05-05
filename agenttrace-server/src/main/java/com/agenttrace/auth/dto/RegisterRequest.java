package com.agenttrace.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "username is required")
        @Size(max = 64, message = "username must be at most 64 characters")
        String username,

        @NotBlank(message = "password is required")
        @Size(min = 6, max = 64, message = "password must be 6 to 64 characters")
        String password,

        @Email(message = "email format is invalid")
        @Size(max = 128, message = "email must be at most 128 characters")
        String email
) {
}

