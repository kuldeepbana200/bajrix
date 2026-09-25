package com.bajrix.backend.service;

import com.bajrix.backend.dto.ProductRequest;
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
            String category,
            Pageable pageable) {

        return productRepository.searchProducts(
                search,
                category,
                pageable);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id));
    }

    @Transactional
    public Product createProduct(ProductRequest request) {

        Product product = new Product();

        product.setName(request.name());
        product.setDescription(request.description());
        product.setCategory(request.category());
        product.setUnit(request.unit());

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long productId, ProductRequest request) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setCategory(request.category());
        product.setUnit(request.unit());

        return productRepository.save(product);
    }
}