package com.bajrix.backend.service;

import com.bajrix.backend.entity.Seller;
import com.bajrix.backend.enums.SellerStatus;
import com.bajrix.backend.exception.ResourceNotFoundException;
import com.bajrix.backend.repository.SellerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SellerService {

    private final SellerRepository sellerRepository;

    public SellerService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    @Transactional
    public Seller createSeller(String name) {

        Seller seller = new Seller(
                name.trim(),
                SellerStatus.PENDING);

        return sellerRepository.save(seller);
    }

    @Transactional(readOnly = true)
    public Seller getSeller(Long sellerId) {

        return sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Seller not found with id: " + sellerId));
    }

    // Approve a seller by changing their status to APPROVED -> We are not implementing ADMIN role for now.
    @Transactional
    public Seller approveSeller(Long sellerId) {

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Seller not found with id: " + sellerId));

        seller.setStatus(SellerStatus.APPROVED);

        return sellerRepository.save(seller);
    }
}