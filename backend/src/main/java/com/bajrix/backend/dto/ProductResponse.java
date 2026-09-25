package com.bajrix.backend.dto;

public record ProductResponse(
        Long id,
        String name,
        String description,
        String category,
        String unit) {
}