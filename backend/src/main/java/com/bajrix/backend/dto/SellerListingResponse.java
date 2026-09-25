package com.bajrix.backend.dto;

import java.math.BigDecimal;

public record SellerListingResponse(
        Long id,
        Long productId,
        String productName,
        Long sellerId,
        String sellerName,
        BigDecimal price,
        Integer stock,
        Integer minimumOrderQuantity,
        String status
) {
}