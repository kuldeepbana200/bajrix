package com.bajrix.backend.dto;

public record AdminSellerResponse(
        Long id,
        String name,
        String status,
        String rejectionReason,
        String contactPerson,
        String email,
        String phone,
        String address,
        String city,
        String state,
        String pincode,
        String gstin,
        String businessType) {
}
