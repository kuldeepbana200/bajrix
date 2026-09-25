package com.bajrix.backend.repository;

import com.bajrix.backend.entity.SellerListing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SellerListingRepository
                extends JpaRepository<SellerListing, Long> {

        List<SellerListing> findBySellerId(Long sellerId);

        List<SellerListing> findByProductId(Long productId);

        Optional<SellerListing> findByIdAndSellerId(
                        Long listingId,
                        Long sellerId);

        boolean existsBySellerIdAndProductId(
                        Long sellerId,
                        Long productId);
}