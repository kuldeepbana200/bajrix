package com.bajrix.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductRequest(

        @NotBlank(message = "Product name is required")
        @Size(max = 200, message = "Product name must be at most 200 characters")
        String name,

        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description,

        @NotBlank(message = "Category is required")
        @Size(max = 100, message = "Category must be at most 100 characters")
        String category,

        @NotBlank(message = "Unit is required")
        @Size(max = 50, message = "Unit must be at most 50 characters")
        String unit
) {
}