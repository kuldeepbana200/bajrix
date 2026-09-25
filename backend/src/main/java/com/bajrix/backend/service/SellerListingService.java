package com.bajrix.backend.service;

import com.bajrix.backend.entity.Product;
import com.bajrix.backend.entity.Seller;
import com.bajrix.backend.entity.SellerListing;
import com.bajrix.backend.enums.ListingStatus;
import com.bajrix.backend.enums.SellerStatus;
import com.bajrix.backend.exception.BusinessException;
import com.bajrix.backend.exception.ResourceNotFoundException;
import com.bajrix.backend.repository.ProductRepository;
import com.bajrix.backend.repository.SellerListingRepository;
import com.bajrix.backend.repository.SellerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SellerListingService {

    private final SellerListingRepository listingRepository;
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;

    public SellerListingService(
            SellerListingRepository listingRepository,
            SellerRepository sellerRepository,
            ProductRepository productRepository) {
        this.listingRepository = listingRepository;
        this.sellerRepository = sellerRepository;
        this.productRepository = productRepository;
    }

    // BUYER
    // Get all active listings for a product, where the seller is approved
    @Transactional(readOnly = true)
    public List<SellerListing> getAvailableListingsForProduct(
            Long productId) {
        // Make sure product exists
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId));

        return listingRepository.findAvailableByProductId(productId);
    }

    // SELLER
    // Get all listings for a seller
    @Transactional(readOnly = true)
    public List<SellerListing> getSellerListings(Long sellerId) {

        getSeller(sellerId);

        return listingRepository.findBySellerId(sellerId);
    }

    // Create a new listing for a seller
    @Transactional
    public SellerListing createListing(
            Long sellerId,
            Long productId,
            BigDecimal price,
            Integer stock,
            Integer minimumOrderQuantity) {

        Seller seller = getSeller(sellerId);

        // Only approved sellers can create listings
        if (seller.getStatus() != SellerStatus.APPROVED) {
            throw new BusinessException(
                    "Only approved sellers can create listings");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId));

        validateListingValues(
                price,
                stock,
                minimumOrderQuantity);

        if (listingRepository.existsBySellerIdAndProductId(
                sellerId,
                productId)) {
            throw new BusinessException(
                    "Seller already has a listing for this product");
        }

        SellerListing listing = new SellerListing();

        listing.setSeller(seller);
        listing.setProduct(product);
        listing.setPrice(price);
        listing.setStock(stock);
        listing.setMinimumOrderQuantity(minimumOrderQuantity);
        listing.setStatus(ListingStatus.ACTIVE);

        return listingRepository.save(listing);
    }

    // Update an existing listing for a seller
    @Transactional
    public SellerListing updateListing(
            Long sellerId,
            Long listingId,
            BigDecimal price,
            Integer stock,
            Integer minimumOrderQuantity) {

        SellerListing listing = listingRepository.findByIdAndSellerId(
                listingId,
                sellerId).orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Listing not found"));

        validateListingValues(
                price,
                stock,
                minimumOrderQuantity);

        listing.setPrice(price);
        listing.setStock(stock);
        listing.setMinimumOrderQuantity(
                minimumOrderQuantity);

        return listingRepository.save(listing);
    }

    // Stop selling a product for a seller
    @Transactional
    public SellerListing stopSelling(
            Long sellerId,
            Long listingId) {

        SellerListing listing = listingRepository.findByIdAndSellerId(
                listingId,
                sellerId).orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Listing not found"));

        listing.setStatus(ListingStatus.STOPPED);

        return listingRepository.save(listing);
    }

    // VALIDATION
    // Validate listing values for creation or update
    private void validateListingValues(
            BigDecimal price,
            Integer stock,
            Integer minimumOrderQuantity) {

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(
                    "Price must be greater than zero");
        }

        if (stock == null || stock < 0) {
            throw new BusinessException(
                    "Stock cannot be negative");
        }

        if (minimumOrderQuantity == null ||
                minimumOrderQuantity <= 0) {

            throw new BusinessException(
                    "Minimum order quantity must be greater than zero");
        }

        if (minimumOrderQuantity > stock) {
            throw new BusinessException(
                    "Minimum order quantity cannot exceed stock");
        }
    }

    private Seller getSeller(Long sellerId) {

        return sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Seller not found with id: " + sellerId));
    }
}