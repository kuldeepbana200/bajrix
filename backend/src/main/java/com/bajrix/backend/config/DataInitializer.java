package com.bajrix.backend.config;

import com.bajrix.backend.entity.Product;
import com.bajrix.backend.entity.Seller;
import com.bajrix.backend.entity.SellerListing;
import com.bajrix.backend.enums.ListingStatus;
import com.bajrix.backend.enums.SellerStatus;
import com.bajrix.backend.repository.ProductRepository;
import com.bajrix.backend.repository.SellerListingRepository;
import com.bajrix.backend.repository.SellerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

        @Bean
        CommandLineRunner initializeData(
                        SellerRepository sellerRepository,
                        ProductRepository productRepository,
                        SellerListingRepository listingRepository) {
                return args -> {

                        System.out.println("======================================");
                        System.out.println("DATA INITIALIZER IS RUNNING");
                        System.out.println("======================================");

                        if (sellerRepository.count() > 0) {
                                System.out.println("Data already exists. Skipping initialization.");
                                return;
                        }

                        // Sellers
                        Seller shreeTraders = sellerRepository.save(
                                        new Seller("Shree Traders", SellerStatus.APPROVED));

                        Seller maaEnterprises = sellerRepository.save(
                                        new Seller("Maa Enterprises", SellerStatus.APPROVED));

                        Seller buildMart = sellerRepository.save(
                                        new Seller("BuildMart", SellerStatus.PENDING));

                        sellerRepository.save(
                                        new Seller("Rejected Supplier", SellerStatus.REJECTED));

                        // Products
                        Product cement = productRepository.save(
                                        new Product(
                                                        "UltraTech PPC Cement 50kg",
                                                        "Portland Pozzolana Cement for construction",
                                                        "Cement",
                                                        "bag"));

                        Product steel = productRepository.save(
                                        new Product(
                                                        "TMT Steel Bar 12mm",
                                                        "12mm TMT steel reinforcement bar",
                                                        "Steel",
                                                        "piece"));

                        Product sand = productRepository.save(
                                        new Product(
                                                        "River Sand",
                                                        "Construction-grade river sand",
                                                        "Sand",
                                                        "ton"));

                        // Listings
                        createListing(
                                        shreeTraders,
                                        cement,
                                        "390.00",
                                        500,
                                        10,
                                        listingRepository);

                        createListing(
                                        maaEnterprises,
                                        cement,
                                        "385.00",
                                        200,
                                        5,
                                        listingRepository);

                        createListing(
                                        buildMart,
                                        cement,
                                        "405.00",
                                        50,
                                        20,
                                        listingRepository);

                        createListing(
                                        shreeTraders,
                                        steel,
                                        "62.00",
                                        1000,
                                        50,
                                        listingRepository);

                        createListing(
                                        maaEnterprises,
                                        steel,
                                        "60.00",
                                        500,
                                        25,
                                        listingRepository);

                        createListing(
                                        shreeTraders,
                                        sand,
                                        "1800.00",
                                        100,
                                        1,
                                        listingRepository);

                        System.out.println("======================================");
                        System.out.println("BajriX SAMPLE DATA CREATED");
                        System.out.println("Sellers: " + sellerRepository.count());
                        System.out.println("Products: " + productRepository.count());
                        System.out.println("Listings: " + listingRepository.count());
                        System.out.println("======================================");
                };
        }

        private void createListing(
                        Seller seller,
                        Product product,
                        String price,
                        int stock,
                        int minimumOrderQuantity,
                        SellerListingRepository repository) {
                SellerListing listing = new SellerListing();

                listing.setSeller(seller);
                listing.setProduct(product);
                listing.setPrice(new BigDecimal(price));
                listing.setStock(stock);
                listing.setMinimumOrderQuantity(minimumOrderQuantity);
                listing.setStatus(ListingStatus.ACTIVE);

                repository.save(listing);
        }
}