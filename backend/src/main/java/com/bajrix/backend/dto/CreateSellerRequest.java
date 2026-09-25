package com.bajrix.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSellerRequest(
        @NotBlank @Size(max = 150) String name) {
}