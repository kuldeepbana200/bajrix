package com.bajrix.backend.controller;

import com.bajrix.backend.dto.AdminLoginRequest;
import com.bajrix.backend.dto.AdminLoginResponse;
import com.bajrix.backend.service.AdminAuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    public AdminAuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @PostMapping("/login")
    public AdminLoginResponse login(
            @Valid @RequestBody AdminLoginRequest request
    ) {
        return adminAuthService.login(
                request.username(),
                request.password()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "X-Admin-Token", required = false)
            String token
    ) {
        adminAuthService.logout(token);
        return ResponseEntity.noContent().build();
    }
}