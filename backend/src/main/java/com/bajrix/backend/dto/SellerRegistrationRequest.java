package com.bajrix.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SellerRegistrationRequest(

        @NotBlank(message = "Business name is required")
        @Size(max = 150, message = "Business name must be at most 150 characters")
        String name,

        @NotBlank(message = "Contact person is required")
        @Size(max = 150, message = "Contact person must be at most 150 characters")
        String contactPerson,

        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email address")
        String email,

        @NotBlank(message = "Phone is required")
        @Pattern(
                regexp = "^[0-9]{10}$",
                message = "Phone must contain exactly 10 digits"
        )
        String phone,

        @NotBlank(message = "Address is required")
        String address,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "State is required")
        String state,

        @NotBlank(message = "Pincode is required")
        @Pattern(
                regexp = "^[0-9]{6}$",
                message = "Pincode must contain exactly 6 digits"
        )
        String pincode,

        @Size(max = 20, message = "GSTIN must be at most 20 characters")
        String gstin,

        @NotBlank(message = "Business type is required")
        String businessType
) {
}