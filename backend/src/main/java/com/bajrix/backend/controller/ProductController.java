package com.bajrix.backend.controller;

import com.bajrix.backend.dto.CreateProductRequest;
import com.bajrix.backend.dto.ProductResponse;
import com.bajrix.backend.dto.SellerListingResponse;
import com.bajrix.backend.entity.Product;
import com.bajrix.backend.entity.SellerListing;
import com.bajrix.backend.service.ProductService;
import com.bajrix.backend.service.SellerListingService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
    public Page<ProductResponse> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(
                page,
                Math.min(size, 100),
                Sort.by("name").ascending());

        return productService
                .getProducts(search, pageable)
                .map(this::toProductResponse);
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
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        Product product = productService.createProduct(
                request.name(),
                request.description(),
                request.category(),
                request.unit());

        return toProductResponse(product);
    }
}