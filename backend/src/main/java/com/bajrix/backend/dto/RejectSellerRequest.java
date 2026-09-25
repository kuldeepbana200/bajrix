package com.bajrix.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record RejectSellerRequest(
        @NotBlank(message = "Rejection reason is required")
        String reason
) {
}