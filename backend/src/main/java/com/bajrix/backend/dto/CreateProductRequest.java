package com.bajrix.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProductRequest(

        @NotBlank(message = "Product name is required") @Size(max = 200, message = "Product name cannot exceed 200 characters") String name,

        String description,

        @Size(max = 100, message = "Category cannot exceed 100 characters") String category,

        @Size(max = 50, message = "Unit cannot exceed 50 characters") String unit) {
}