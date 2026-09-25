package com.bajrix.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateListingRequest(

        @NotNull Long productId,

        @NotNull @DecimalMin(value = "0.01") BigDecimal price,

        @NotNull @Min(0) Integer stock,

        @NotNull @Min(1) Integer minimumOrderQuantity) {
}