package com.bajrix.backend.service;

import com.bajrix.backend.dto.AdminLoginResponse;
import com.bajrix.backend.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class AdminAuthService {

    private final String adminUsername;
    private final String adminPassword;

    private final ConcurrentMap<String, String> activeTokens = new ConcurrentHashMap<>();

    public AdminAuthService(
            @Value("${bajrix.admin.username}") String adminUsername,
            @Value("${bajrix.admin.password}") String adminPassword) {
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    public AdminLoginResponse login(String username, String password) {

        if (!adminUsername.equals(username)
                || !adminPassword.equals(password)) {

            throw new BusinessException("Invalid admin username or password");
        }

        String token = UUID.randomUUID().toString();

        activeTokens.put(token, username);

        return new AdminLoginResponse(
                token,
                username,
                "ADMIN");
    }

    public boolean isValidToken(String token) {
        return token != null && activeTokens.containsKey(token);
    }

    public void logout(String token) {
        if (token != null) {
            activeTokens.remove(token);
        }
    }
}