package com.bajrix.backend.service;

import com.bajrix.backend.dto.CreateListingRequest;
import com.bajrix.backend.dto.UpdateListingRequest;
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

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerListingServiceTest {

    @Mock
    private SellerListingRepository sellerListingRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SellerListingService sellerListingService;

    private Seller approvedSeller;
    private Seller pendingSeller;
    private Seller rejectedSeller;
    private Product product;

    @BeforeEach
    void setUp() {

        approvedSeller = new Seller(
                "Approved Seller",
                SellerStatus.APPROVED);

        pendingSeller = new Seller(
                "Pending Seller",
                SellerStatus.PENDING);

        rejectedSeller = new Seller(
                "Rejected Seller",
                SellerStatus.REJECTED);

        product = new Product();
        product.setName("Cement");
        product.setCategory("Cement");
        product.setUnit("50kg bag");
    }

    // =========================================================
    // 1. APPROVED SELLER LISTING IS VISIBLE
    // =========================================================

    @Test
    void approvedSellerListingShouldBeVisible() {

        SellerListing listing = createListing(
                approvedSeller,
                product,
                new BigDecimal("390.00"),
                500,
                10,
                ListingStatus.ACTIVE);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(sellerListingRepository.findAvailableByProductId(1L))
                .thenReturn(List.of(listing));

        List<SellerListing> result = sellerListingService.getAvailableListingsForProduct(1L);

        assertEquals(1, result.size());

        assertEquals(
                ListingStatus.ACTIVE,
                result.get(0).getStatus());
    }

    // =========================================================
    // 2. PENDING SELLER LISTING IS HIDDEN
    // =========================================================

    @Test
    void pendingSellerListingShouldBeHidden() {

        SellerListing listing = createListing(
                pendingSeller,
                product,
                new BigDecimal("390.00"),
                500,
                10,
                ListingStatus.ACTIVE);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(sellerListingRepository.findAvailableByProductId(1L))
                .thenReturn(List.of(listing));

        List<SellerListing> result = sellerListingService.getAvailableListingsForProduct(1L);

        assertTrue(result.isEmpty());
    }

    // =========================================================
    // 3. STOPPED LISTING IS HIDDEN
    // =========================================================

    @Test
    void stoppedListingShouldBeHidden() {

        SellerListing listing = createListing(
                approvedSeller,
                product,
                new BigDecimal("390.00"),
                500,
                10,
                ListingStatus.STOPPED);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(sellerListingRepository.findAvailableByProductId(1L))
                .thenReturn(List.of(listing));

        List<SellerListing> result = sellerListingService.getAvailableListingsForProduct(1L);

        assertTrue(result.isEmpty());
    }

    // =========================================================
    // 4. SELLER CAN UPDATE OWN LISTING
    // =========================================================

    @Test
    void sellerCanUpdateOwnListing() {

        SellerListing listing = createListing(
                approvedSeller,
                product,
                new BigDecimal("390.00"),
                500,
                10,
                ListingStatus.ACTIVE);

        when(sellerListingRepository.findByIdAndSellerId(1L, 1L))
                .thenReturn(Optional.of(listing));

        UpdateListingRequest request = new UpdateListingRequest(
                new BigDecimal("385.00"),
                400,
                20);

        when(sellerListingRepository.save(any(SellerListing.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SellerListing result = sellerListingService.updateListing(
                1L,
                1L,
                request.price(),
                request.stock(),
                request.minimumOrderQuantity());

        assertEquals(
                new BigDecimal("385.00"),
                result.getPrice());

        assertEquals(400, result.getStock());

        assertEquals(
                20,
                result.getMinimumOrderQuantity());

        verify(sellerListingRepository).save(listing);
    }

    // =========================================================
    // 5. SELLER CANNOT UPDATE ANOTHER SELLER'S LISTING
    // =========================================================

    @Test
    void sellerCannotUpdateAnotherSellersListing() {

        when(sellerListingRepository.findByIdAndSellerId(1L, 999L))
                .thenReturn(Optional.empty());

        UpdateListingRequest request = new UpdateListingRequest(
                new BigDecimal("385.00"),
                400,
                20);

        assertThrows(
                ResourceNotFoundException.class,
                () -> sellerListingService.updateListing(
                        999L,
                        1L,
                        request.price(),
                        request.stock(),
                        request.minimumOrderQuantity()));

        verify(
                sellerListingRepository,
                never()).save(any(SellerListing.class));
    }

    // =========================================================
    // 6. DUPLICATE SELLER/PRODUCT LISTING IS REJECTED
    // =========================================================

    @Test
    void duplicateSellerProductListingShouldBeRejected() {

        when(sellerRepository.findById(1L))
                .thenReturn(Optional.of(approvedSeller));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(
                sellerListingRepository
                        .existsBySellerIdAndProductId(1L, 1L))
                .thenReturn(true);

        CreateListingRequest request = new CreateListingRequest(
                1L,
                new BigDecimal("390.00"),
                500,
                10);

        assertThrows(
                BusinessException.class,
                () -> sellerListingService.createListing(
                        1L,
                        request.productId(),
                        request.price(),
                        request.stock(),
                        request.minimumOrderQuantity()));

        verify(
                sellerListingRepository,
                never()).save(any(SellerListing.class));
    }

    // =========================================================
    // 7. NEGATIVE PRICE IS REJECTED
    // =========================================================

    @Test
    void negativePriceShouldBeRejected() {

        when(sellerRepository.findById(1L))
                .thenReturn(Optional.of(approvedSeller));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        CreateListingRequest request = new CreateListingRequest(
                1L,
                new BigDecimal("-10.00"),
                500,
                10);

        assertThrows(
                BusinessException.class,
                () -> sellerListingService.createListing(
                        1L,
                        request.productId(),
                        request.price(),
                        request.stock(),
                        request.minimumOrderQuantity()));

        verify(
                sellerListingRepository,
                never()).save(any(SellerListing.class));
    }

    // =========================================================
    // 8. NEGATIVE STOCK IS REJECTED
    // =========================================================

    @Test
    void negativeStockShouldBeRejected() {

        when(sellerRepository.findById(1L))
                .thenReturn(Optional.of(approvedSeller));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        CreateListingRequest request = new CreateListingRequest(
                1L,
                new BigDecimal("390.00"),
                -10,
                5);

        assertThrows(
                BusinessException.class,
                () -> sellerListingService.createListing(
                        1L,
                        request.productId(),
                        request.price(),
                        request.stock(),
                        request.minimumOrderQuantity()));

        verify(
                sellerListingRepository,
                never()).save(any(SellerListing.class));
    }

    // =========================================================
    // 9. MOQ GREATER THAN STOCK IS REJECTED
    // =========================================================

    @Test
    void minimumOrderQuantityGreaterThanStockShouldBeRejected() {

        when(sellerRepository.findById(1L))
                .thenReturn(Optional.of(approvedSeller));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        CreateListingRequest request = new CreateListingRequest(
                1L,
                new BigDecimal("390.00"),
                5,
                10);

        assertThrows(
                BusinessException.class,
                () -> sellerListingService.createListing(
                        1L,
                        request.productId(),
                        request.price(),
                        request.stock(),
                        request.minimumOrderQuantity()));

        verify(
                sellerListingRepository,
                never()).save(any(SellerListing.class));
    }

    // =========================================================
    // 10. PENDING SELLER CANNOT CREATE LISTING
    // =========================================================

    @Test
    void pendingSellerCannotCreateListing() {

        when(sellerRepository.findById(1L))
                .thenReturn(Optional.of(pendingSeller));

        assertThrows(
                BusinessException.class,
                () -> sellerListingService.createListing(
                        1L,
                        1L,
                        new BigDecimal("390.00"),
                        500,
                        10));

        verify(
                sellerListingRepository,
                never()).save(any(SellerListing.class));
    }

    // =========================================================
    // 11. REJECTED SELLER CANNOT CREATE LISTING
    // =========================================================

    @Test
    void rejectedSellerCannotCreateListing() {

        when(sellerRepository.findById(1L))
                .thenReturn(Optional.of(rejectedSeller));

        assertThrows(
                BusinessException.class,
                () -> sellerListingService.createListing(
                        1L,
                        1L,
                        new BigDecimal("390.00"),
                        500,
                        10));

        verify(
                sellerListingRepository,
                never()).save(any(SellerListing.class));
    }

    // =========================================================
    // 12. APPROVED SELLER CAN CREATE VALID LISTING
    // =========================================================

    @Test
    void approvedSellerCanCreateValidListing() {

        when(sellerRepository.findById(1L))
                .thenReturn(Optional.of(approvedSeller));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(
                sellerListingRepository
                        .existsBySellerIdAndProductId(1L, 1L))
                .thenReturn(false);

        when(sellerListingRepository.save(any(SellerListing.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SellerListing result = sellerListingService.createListing(
                1L,
                1L,
                new BigDecimal("390.00"),
                500,
                10);

        assertNotNull(result);

        assertEquals(
                approvedSeller,
                result.getSeller());

        assertEquals(
                product,
                result.getProduct());

        assertEquals(
                new BigDecimal("390.00"),
                result.getPrice());

        assertEquals(500, result.getStock());

        assertEquals(
                10,
                result.getMinimumOrderQuantity());

        assertEquals(
                ListingStatus.ACTIVE,
                result.getStatus());

        verify(
                sellerListingRepository).save(any(SellerListing.class));
    }

    // =========================================================
    // 13. NON-EXISTENT PRODUCT IS REJECTED
    // =========================================================

    @Test
    void nonExistentProductShouldBeRejected() {

        when(sellerRepository.findById(1L))
                .thenReturn(Optional.of(approvedSeller));

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> sellerListingService.createListing(
                        1L,
                        999L,
                        new BigDecimal("390.00"),
                        500,
                        10));

        verify(
                sellerListingRepository,
                never()).save(any(SellerListing.class));
    }

    // =========================================================
    // 14. SELLER NOT FOUND
    // =========================================================

    @Test
    void nonExistentSellerShouldBeRejected() {

        when(sellerRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> sellerListingService.createListing(
                        999L,
                        1L,
                        new BigDecimal("390.00"),
                        500,
                        10));

        verify(
                sellerListingRepository,
                never()).save(any(SellerListing.class));
    }

    // =========================================================
    // 15. PRODUCT NOT FOUND WHEN CHECKING AVAILABILITY
    // =========================================================

    @Test
    void nonExistentProductShouldFailAvailabilityCheck() {

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> sellerListingService
                        .getAvailableListingsForProduct(999L));

        verify(
                sellerListingRepository,
                never()).findAvailableByProductId(999L);
    }

    // =========================================================
    // 16. INVALID PRICE DURING UPDATE
    // =========================================================

    @Test
    void negativePriceShouldBeRejectedDuringUpdate() {

        SellerListing listing = createListing(
                approvedSeller,
                product,
                new BigDecimal("390.00"),
                500,
                10,
                ListingStatus.ACTIVE);

        when(
                sellerListingRepository
                        .findByIdAndSellerId(1L, 1L))
                .thenReturn(Optional.of(listing));

        assertThrows(
                BusinessException.class,
                () -> sellerListingService.updateListing(
                        1L,
                        1L,
                        new BigDecimal("-10.00"),
                        500,
                        10));

        verify(
                sellerListingRepository,
                never()).save(any(SellerListing.class));
    }

    // =========================================================
    // 17. MOQ GREATER THAN STOCK DURING UPDATE
    // =========================================================

    @Test
    void minimumOrderQuantityGreaterThanStockShouldBeRejectedDuringUpdate() {

        SellerListing listing = createListing(
                approvedSeller,
                product,
                new BigDecimal("390.00"),
                100,
                10,
                ListingStatus.ACTIVE);

        when(
                sellerListingRepository
                        .findByIdAndSellerId(1L, 1L))
                .thenReturn(Optional.of(listing));

        assertThrows(
                BusinessException.class,
                () -> sellerListingService.updateListing(
                        1L,
                        1L,
                        new BigDecimal("390.00"),
                        5,
                        10));

        verify(
                sellerListingRepository,
                never()).save(any(SellerListing.class));
    }

    // =========================================================
    // 18. SELLER CAN STOP OWN LISTING
    // =========================================================

    @Test
    void sellerCanStopOwnListing() {

        SellerListing listing = createListing(
                approvedSeller,
                product,
                new BigDecimal("390.00"),
                500,
                10,
                ListingStatus.ACTIVE);

        when(
                sellerListingRepository
                        .findByIdAndSellerId(1L, 1L))
                .thenReturn(Optional.of(listing));

        when(
                sellerListingRepository.save(any(SellerListing.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SellerListing result = sellerListingService.stopSelling(
                1L,
                1L);

        assertEquals(
                ListingStatus.STOPPED,
                result.getStatus());

        verify(
                sellerListingRepository).save(listing);
    }

    // =========================================================
    // 19. SELLER CANNOT STOP ANOTHER SELLER'S LISTING
    // =========================================================

    @Test
    void sellerCannotStopAnotherSellersListing() {

        when(
                sellerListingRepository
                        .findByIdAndSellerId(1L, 999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> sellerListingService.stopSelling(
                        999L,
                        1L));

        verify(
                sellerListingRepository,
                never()).save(any(SellerListing.class));
    }
    // =========================================================
    // 20. STOPPED LISTING REMAINS STOPPED
    // =========================================================

    @Test
    void alreadyStoppedListingRemainsStopped() {

        SellerListing listing = createListing(
                approvedSeller,
                product,
                new BigDecimal("390.00"),
                500,
                10,
                ListingStatus.STOPPED);

        when(
                sellerListingRepository
                        .findByIdAndSellerId(1L, 1L))
                .thenReturn(Optional.of(listing));

        when(
                sellerListingRepository.save(any(SellerListing.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SellerListing result = sellerListingService.stopSelling(
                1L,
                1L);

        assertEquals(
                ListingStatus.STOPPED,
                result.getStatus());

        verify(
                sellerListingRepository).save(listing);
    }

    // =========================================================
    // HELPER
    // =========================================================

    private SellerListing createListing(
            Seller seller,
            Product product,
            BigDecimal price,
            int stock,
            int minimumOrderQuantity,
            ListingStatus status) {

        SellerListing listing = new SellerListing();

        listing.setSeller(seller);
        listing.setProduct(product);
        listing.setPrice(price);
        listing.setStock(stock);
        listing.setMinimumOrderQuantity(minimumOrderQuantity);
        listing.setStatus(status);

        return listing;
    }
}