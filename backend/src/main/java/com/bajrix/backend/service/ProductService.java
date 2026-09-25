package com.bajrix.backend.service;

import com.bajrix.backend.entity.Product;
import com.bajrix.backend.exception.ResourceNotFoundException;
import com.bajrix.backend.repository.ProductRepository;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> getProducts(
            String search,
            Pageable pageable) {
        if (search == null || search.isBlank()) {
            return productRepository.findAll(pageable);
        }

        return productRepository.findByNameContainingIgnoreCase(
                search.trim(),
                pageable);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id));
    }

    @Transactional
    public Product createProduct(
            String name,
            String description,
            String category,
            String unit) {
        Product product = new Product(
                name.trim(),
                description,
                category,
                unit);

        return productRepository.save(product);
    }
}