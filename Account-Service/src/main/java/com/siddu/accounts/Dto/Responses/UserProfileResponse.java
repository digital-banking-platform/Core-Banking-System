package com.siddu.accounts.Dto.Responses;

import com.siddu.accounts.Enums.KycStatus;

import java.time.LocalDate;
import java.util.UUID;

public record UserProfileResponse(
        UUID profileId,
        String accountHolderName,
        LocalDate dateOfBirth,
        String phoneNumber,
        String address,
        String city,
        String state,
        String pincode,
        KycStatus kycStatus,
        java.time.Instant updatedAt

) {}
