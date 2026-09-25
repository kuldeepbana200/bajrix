package com.bajrix.backend.controller;

import com.bajrix.backend.dto.RejectSellerRequest;
import com.bajrix.backend.dto.AdminSellerResponse;
import com.bajrix.backend.entity.Seller;
import com.bajrix.backend.service.SellerService;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import java.util.List;

@SecurityRequirement(name = "adminToken")
@RestController
@RequestMapping("/api/admin/sellers")
public class AdminSellerController {

    private final SellerService sellerService;

    public AdminSellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @GetMapping
    public List<AdminSellerResponse> getAllSellers() {
        return sellerService.getAllSellers()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PatchMapping("/{id}/approve")
    public AdminSellerResponse approveSeller(@PathVariable Long id) {
        return toResponse(sellerService.approveSeller(id));
    }

    @PatchMapping("/{id}/reject")
    public AdminSellerResponse rejectSeller(
            @PathVariable Long id,
            @Valid @RequestBody RejectSellerRequest request) {
        return toResponse(sellerService.rejectSeller(id, request.reason()));
    }

    private AdminSellerResponse toResponse(Seller seller) {
        return new AdminSellerResponse(
                seller.getId(),
                seller.getName(),
                seller.getStatus().name(),
                seller.getRejectionReason(),
                seller.getContactPerson(),
                seller.getEmail(),
                seller.getPhone(),
                seller.getAddress(),
                seller.getCity(),
                seller.getState(),
                seller.getPincode(),
                seller.getGstin(),
                seller.getBusinessType());
    }
}