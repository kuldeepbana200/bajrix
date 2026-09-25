package com.bajrix.backend.controller;

import com.bajrix.backend.dto.CreateSellerRequest;
import com.bajrix.backend.dto.SellerResponse;
import com.bajrix.backend.entity.Seller;
import com.bajrix.backend.service.SellerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sellers")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SellerResponse createSeller(
            @Valid @RequestBody CreateSellerRequest request) {
        Seller seller = sellerService.createSeller(request.name());

        return toResponse(seller);
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

    @PatchMapping("/{sellerId}/approve")
    public SellerResponse approveSeller(
            @PathVariable Long sellerId) {
        return toResponse(
                sellerService.approveSeller(sellerId));
    }
}