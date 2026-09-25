package com.bajrix.backend.dto;

public record AdminLoginResponse(
        String token,
        String username,
        String role
) {
}