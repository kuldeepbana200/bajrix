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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerListingServiceTest {

    @Mock
    private SellerListingRepository listingRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private ProductRepository productRepository;

    private SellerListingService listingService;

    private Seller approvedSeller;
    private Seller pendingSeller;
    private Seller secondSeller;
    private Product product;

    @BeforeEach
    void setUp() {
        listingService = new SellerListingService(
                listingRepository,
                sellerRepository,
                productRepository);

        approvedSeller = new Seller(
                "Shree Traders",
                SellerStatus.APPROVED);

        pendingSeller = new Seller(
                "Pending Seller",
                SellerStatus.PENDING);

        secondSeller = new Seller(
                "Maa Enterprises",
                SellerStatus.APPROVED);

        product = new Product(
                "Cement 50kg",
                "Construction cement",
                "Cement",
                "bag");
    }

    @Test
    void approvedSellerCanCreateListing() {

        when(sellerRepository.findById(1L))
                .thenReturn(Optional.of(approvedSeller));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(listingRepository.existsBySellerIdAndProductId(1L, 1L))
                .thenReturn(false);

        SellerListing savedListing = new SellerListing();
        savedListing.setSeller(approvedSeller);
        savedListing.setProduct(product);
        savedListing.setPrice(new BigDecimal("400.00"));
        savedListing.setStock(100);
        savedListing.setMinimumOrderQuantity(10);
        savedListing.setStatus(ListingStatus.ACTIVE);

        when(listingRepository.save(any(SellerListing.class)))
                .thenReturn(savedListing);

        SellerListing result = listingService.createListing(
                1L,
                1L,
                new BigDecimal("400.00"),
                100,
                10);

        assertEquals(new BigDecimal("400.00"), result.getPrice());
        assertEquals(100, result.getStock());
        assertEquals(10, result.getMinimumOrderQuantity());

        verify(listingRepository).save(any(SellerListing.class));
    }

    @Test
    void pendingSellerCannotCreateListing() {

        when(sellerRepository.findById(1L))
                .thenReturn(Optional.of(pendingSeller));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> listingService.createListing(
                        1L,
                        1L,
                        new BigDecimal("400.00"),
                        100,
                        10));

        assertEquals(
                "Only approved sellers can create listings",
                exception.getMessage());

        verify(listingRepository, never())
                .save(any(SellerListing.class));
    }

    @Test
    void duplicateSellerProductListingIsRejected() {

        when(sellerRepository.findById(1L))
                .thenReturn(Optional.of(approvedSeller));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(listingRepository.existsBySellerIdAndProductId(1L, 1L))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> listingService.createListing(
                        1L,
                        1L,
                        new BigDecimal("400.00"),
                        100,
                        10));

        assertEquals(
                "Seller already has a listing for this product",
                exception.getMessage());

        verify(listingRepository, never())
                .save(any(SellerListing.class));
    }

    @Test
    void negativePriceIsRejected() {

        when(sellerRepository.findById(1L))
                .thenReturn(Optional.of(approvedSeller));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> listingService.createListing(
                        1L,
                        1L,
                        new BigDecimal("-10.00"),
                        100,
                        10));

        assertEquals(
                "Price must be greater than zero",
                exception.getMessage());
    }

    @Test
    void negativeStockIsRejected() {

        when(sellerRepository.findById(1L))
                .thenReturn(Optional.of(approvedSeller));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> listingService.createListing(
                        1L,
                        1L,
                        new BigDecimal("400.00"),
                        -1,
                        1));

        assertEquals(
                "Stock cannot be negative",
                exception.getMessage());
    }

    @Test
    void minimumOrderQuantityGreaterThanStockIsRejected() {

        when(sellerRepository.findById(1L))
                .thenReturn(Optional.of(approvedSeller));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> listingService.createListing(
                        1L,
                        1L,
                        new BigDecimal("400.00"),
                        5,
                        10));

        assertEquals(
                "Minimum order quantity cannot exceed stock",
                exception.getMessage());
    }

    @Test
    void sellerCanUpdateOwnListing() {

        SellerListing listing = new SellerListing();
        listing.setSeller(approvedSeller);
        listing.setProduct(product);
        listing.setPrice(new BigDecimal("400.00"));
        listing.setStock(100);
        listing.setMinimumOrderQuantity(10);
        listing.setStatus(ListingStatus.ACTIVE);

        when(listingRepository.findByIdAndSellerId(1L, 1L))
                .thenReturn(Optional.of(listing));

        when(listingRepository.save(any(SellerListing.class)))
                .thenReturn(listing);

        SellerListing result = listingService.updateListing(
                1L,
                1L,
                new BigDecimal("390.00"),
                80,
                5);

        assertEquals(new BigDecimal("390.00"), result.getPrice());
        assertEquals(80, result.getStock());
        assertEquals(5, result.getMinimumOrderQuantity());

        verify(listingRepository)
                .findByIdAndSellerId(1L, 1L);
    }

    @Test
    void sellerCannotUpdateAnotherSellersListing() {

        when(listingRepository.findByIdAndSellerId(1L, 2L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> listingService.updateListing(
                        2L,
                        1L,
                        new BigDecimal("100.00"),
                        10,
                        1));

        verify(listingRepository, never())
                .save(any(SellerListing.class));
    }

    @Test
    void stoppedListingIsHiddenFromBuyers() {

        SellerListing activeListing = new SellerListing();
        activeListing.setSeller(approvedSeller);
        activeListing.setProduct(product);
        activeListing.setStatus(ListingStatus.ACTIVE);

        SellerListing stoppedListing = new SellerListing();
        stoppedListing.setSeller(secondSeller);
        stoppedListing.setProduct(product);
        stoppedListing.setStatus(ListingStatus.STOPPED);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(listingRepository.findByProductId(1L))
                .thenReturn(List.of(
                        activeListing,
                        stoppedListing));

        List<SellerListing> result = listingService.getAvailableListingsForProduct(1L);

        assertEquals(1, result.size());
        assertSame(activeListing, result.get(0));
    }

    @Test
    void pendingSellerListingIsHiddenFromBuyers() {

        SellerListing approvedListing = new SellerListing();
        approvedListing.setSeller(approvedSeller);
        approvedListing.setProduct(product);
        approvedListing.setStatus(ListingStatus.ACTIVE);

        SellerListing pendingListing = new SellerListing();
        pendingListing.setSeller(pendingSeller);
        pendingListing.setProduct(product);
        pendingListing.setStatus(ListingStatus.ACTIVE);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(listingRepository.findByProductId(1L))
                .thenReturn(List.of(
                        approvedListing,
                        pendingListing));

        List<SellerListing> result = listingService.getAvailableListingsForProduct(1L);

        assertEquals(1, result.size());
        assertSame(approvedListing, result.get(0));
    }

    @Test
    void sellerCanStopOwnListing() {

        SellerListing listing = new SellerListing();
        listing.setSeller(approvedSeller);
        listing.setProduct(product);
        listing.setPrice(new BigDecimal("400.00"));
        listing.setStock(100);
        listing.setMinimumOrderQuantity(10);
        listing.setStatus(ListingStatus.ACTIVE);

        when(listingRepository.findByIdAndSellerId(1L, 1L))
                .thenReturn(Optional.of(listing));

        when(listingRepository.save(any(SellerListing.class)))
                .thenReturn(listing);

        SellerListing result = listingService.stopSelling(1L, 1L);

        assertEquals(
                ListingStatus.STOPPED,
                result.getStatus());

        verify(listingRepository)
                .findByIdAndSellerId(1L, 1L);

        verify(listingRepository)
                .save(listing);
    }

    @Test
    void sellerCannotStopAnotherSellersListing() {

        when(listingRepository.findByIdAndSellerId(1L, 2L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> listingService.stopSelling(2L, 1L));

        verify(listingRepository, never())
                .save(any(SellerListing.class));
    }
}