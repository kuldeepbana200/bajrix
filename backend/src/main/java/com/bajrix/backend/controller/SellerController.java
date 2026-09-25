package com.bajrix.backend.controller;

import com.bajrix.backend.dto.SellerRegistrationRequest;
import com.bajrix.backend.dto.SellerResponse;
import com.bajrix.backend.entity.Seller;
import com.bajrix.backend.service.SellerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sellers")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @PostMapping
    public Seller createSeller(
            @Valid @RequestBody SellerRegistrationRequest request) {

        return sellerService.createSeller(request);
    }

    @GetMapping("/{sellerId}")
    public SellerResponse getSeller(
            @PathVariable Long sellerId) {
        return toResponse(
                sellerService.getSeller(sellerId));
    }

    private SellerResponse toResponse(Seller seller) {

        return new SellerResponse(
                seller.getId(),
                seller.getName(),
                seller.getStatus().name());
    }

    // Get all sellers to show it on the dashboard.
    @GetMapping
    public java.util.List<SellerResponse> getAllSellers() {
        return sellerService.getAllSellers()
                .stream()
                .map(this::toResponse)
                .toList();
    }

}