package com.siddu.accounts.Dto.Responses;

import com.siddu.accounts.Enums.KycStatus;

import java.time.LocalDate;

public record ProfileResponse(
        String accountHolderName,
        LocalDate dateOfBirth,
        String phoneNumber,
        String address,
        String city,
        String state,
        String pincode,
        KycStatus kycStatus
) {}

