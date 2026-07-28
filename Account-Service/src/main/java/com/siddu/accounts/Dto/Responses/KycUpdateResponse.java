package com.siddu.accounts.Dto.Responses;

import com.siddu.accounts.Enums.KycStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record KycUpdateResponse(
        UUID ProfileId,
        String AccountHolderName,
        KycStatus kycStatus,
        Instant updatedAt


) { }
