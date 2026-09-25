package com.bajrix.backend.controller;

import com.bajrix.backend.dto.CreateListingRequest;
import com.bajrix.backend.dto.SellerListingResponse;

import com.bajrix.backend.dto.UpdateListingRequest;
import com.bajrix.backend.entity.SellerListing;
import com.bajrix.backend.service.SellerListingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller/listings")
public class SellerListingController {

    private final SellerListingService listingService;

    public SellerListingController(
            SellerListingService listingService
    ) {
        this.listingService = listingService;
    }

    // Get all listings for the authenticated seller
    @GetMapping
    public List<SellerListingResponse> getMyListings(
            @RequestHeader("X-Seller-Id") Long sellerId
    ) {
        return listingService
                .getSellerListings(sellerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SellerListingResponse createListing(
            @RequestHeader("X-Seller-Id") Long sellerId,
            @Valid @RequestBody CreateListingRequest request
    ) {

        SellerListing listing =
                listingService.createListing(
                        sellerId,
                        request.productId(),
                        request.price(),
                        request.stock(),
                        request.minimumOrderQuantity()
                );

        return toResponse(listing);
    }

    @PutMapping("/{listingId}")
    public SellerListingResponse updateListing(
            @RequestHeader("X-Seller-Id") Long sellerId,
            @PathVariable Long listingId,
            @Valid @RequestBody UpdateListingRequest request
    ) {

        SellerListing listing =
                listingService.updateListing(
                        sellerId,
                        listingId,
                        request.price(),
                        request.stock(),
                        request.minimumOrderQuantity()
                );

        return toResponse(listing);
    }

    @PatchMapping("/{listingId}/stop")
    public SellerListingResponse stopSelling(
            @RequestHeader("X-Seller-Id") Long sellerId,
            @PathVariable Long listingId
    ) {

        SellerListing listing =
                listingService.stopSelling(
                        sellerId,
                        listingId
                );

        return toResponse(listing);
    }

    private SellerListingResponse toResponse(
            SellerListing listing
    ) {
        return new SellerListingResponse(
                listing.getId(),
                listing.getProduct().getId(),
                listing.getProduct().getName(),
                listing.getSeller().getId(),
                listing.getSeller().getName(),
                listing.getPrice(),
                listing.getStock(),
                listing.getMinimumOrderQuantity(),
                listing.getStatus().name()
        );
    }
}