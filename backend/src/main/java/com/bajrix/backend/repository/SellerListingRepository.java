package com.bajrix.backend.repository;

import com.bajrix.backend.entity.SellerListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SellerListingRepository
                extends JpaRepository<SellerListing, Long> {

        List<SellerListing> findBySellerId(Long sellerId);

        List<SellerListing> findByProductId(Long productId);

        @Query("""
                        SELECT listing
                        FROM SellerListing listing
                        JOIN FETCH listing.product product
                        JOIN FETCH listing.seller seller
                        WHERE product.id = :productId
                                AND listing.status = com.bajrix.backend.enums.ListingStatus.ACTIVE
                                AND seller.status = com.bajrix.backend.enums.SellerStatus.APPROVED
                        """)
        List<SellerListing> findAvailableByProductId(
                        @Param("productId") Long productId);

        Optional<SellerListing> findByIdAndSellerId(
                        Long listingId,
                        Long sellerId);

        boolean existsBySellerIdAndProductId(
                        Long sellerId,
                        Long productId);
}