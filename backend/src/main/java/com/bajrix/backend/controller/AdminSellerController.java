package com.bajrix.backend.controller;

import com.bajrix.backend.dto.RejectSellerRequest;
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
    public List<Seller> getAllSellers() {
        return sellerService.getAllSellers();
    }

    @PatchMapping("/{id}/approve")
    public Seller approveSeller(@PathVariable Long id) {
        return sellerService.approveSeller(id);
    }

    @PatchMapping("/{id}/reject")
    public Seller rejectSeller(
            @PathVariable Long id,
            @Valid @RequestBody RejectSellerRequest request) {
        return sellerService.rejectSeller(id, request.reason());
    }
}