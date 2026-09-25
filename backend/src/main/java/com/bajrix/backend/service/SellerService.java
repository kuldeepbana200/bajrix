package com.bajrix.backend.service;

import com.bajrix.backend.dto.SellerRegistrationRequest;
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
    public Seller createSeller(SellerRegistrationRequest request) {

        Seller seller = new Seller(
                request.name(),
                SellerStatus.PENDING);

        seller.setContactPerson(request.contactPerson());
        seller.setEmail(request.email());
        seller.setPhone(request.phone());
        seller.setAddress(request.address());
        seller.setCity(request.city());
        seller.setState(request.state());
        seller.setPincode(request.pincode());
        seller.setGstin(request.gstin());
        seller.setBusinessType(request.businessType());

        return sellerRepository.save(seller);
    }

    @Transactional(readOnly = true)
    public Seller getSeller(Long sellerId) {

        return sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Seller not found with id: " + sellerId));
    }

    // Get all sellers to show it on the dashboard.
    @Transactional(readOnly = true)
    public java.util.List<Seller> getAllSellers() {
        return sellerRepository.findAll();
    }

    // Approve a seller by changing their status to APPROVED -> We are not
    // implementing ADMIN role for now.
    public Seller approveSeller(Long sellerId) {

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        seller.setStatus(SellerStatus.APPROVED);
        seller.setRejectionReason(null);

        return sellerRepository.save(seller);
    }

    public Seller rejectSeller(Long sellerId, String reason) {

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));

        seller.setStatus(SellerStatus.REJECTED);
        seller.setRejectionReason(reason);

        return sellerRepository.save(seller);
    }

}