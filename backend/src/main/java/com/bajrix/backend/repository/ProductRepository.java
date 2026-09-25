package com.bajrix.backend.repository;

import com.bajrix.backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            SELECT p
            FROM Product p
            WHERE (:search IS NULL OR :search = ''
                   OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:category IS NULL OR :category = ''
                   OR LOWER(p.category) = LOWER(:category))
            """)
    Page<Product> searchProducts(
            @Param("search") String search,
            @Param("category") String category,
            Pageable pageable);
}