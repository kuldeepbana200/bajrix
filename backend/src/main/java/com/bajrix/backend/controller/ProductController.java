package com.bajrix.backend.controller;

import com.bajrix.backend.dto.ProductRequest;
import com.bajrix.backend.dto.ProductResponse;
import com.bajrix.backend.dto.SellerListingResponse;
import com.bajrix.backend.entity.Product;
import com.bajrix.backend.entity.SellerListing;
import com.bajrix.backend.service.ProductService;
import com.bajrix.backend.service.SellerListingService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final SellerListingService listingService;

    public ProductController(
            ProductService productService,
            SellerListingService listingService) {
        this.productService = productService;
        this.listingService = listingService;
    }

    // Get a paginated list of products with optional search
    @GetMapping
    public Page<Product> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        return productService.getProducts(
                search,
                category,
                pageable);
    }

    // Get a specific product by its ID
    @GetMapping("/{id}")
    public ProductResponse getProduct(
            @PathVariable Long id) {
        return toProductResponse(
                productService.getProductById(id));
    }

    // Get all available listings for a specific product
    @GetMapping("/{id}/listings")
    public List<SellerListingResponse> getListings(
            @PathVariable Long id) {
        return listingService
                .getAvailableListingsForProduct(id)
                .stream()
                .map(this::toListingResponse)
                .toList();
    }

    private ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getUnit());
    }

    private SellerListingResponse toListingResponse(
            SellerListing listing) {
        return new SellerListingResponse(
                listing.getId(),
                listing.getProduct().getId(),
                listing.getProduct().getName(),
                listing.getSeller().getId(),
                listing.getSeller().getName(),
                listing.getPrice(),
                listing.getStock(),
                listing.getMinimumOrderQuantity(),
                listing.getStatus().name());
    }

    @PostMapping
    @SecurityRequirement(name = "adminToken")
    public Product createProduct(
            @Valid @RequestBody ProductRequest request) {

        return productService.createProduct(request);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "adminToken")
    public Product updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        return productService.updateProduct(id, request);
    }
}