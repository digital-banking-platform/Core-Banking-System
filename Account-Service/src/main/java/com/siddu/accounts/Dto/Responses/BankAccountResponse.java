package com.siddu.accounts.Dto.Responses;

import com.siddu.accounts.Enums.AccountStatus;
import com.siddu.accounts.Enums.AccountType;
import com.siddu.accounts.Enums.Gender;
import com.siddu.accounts.Enums.KycStatus;

import java.time.LocalDate;

public record BankAccountResponse(

        String accountNumber,
        AccountType accountType,
        AccountStatus accountStatus,

        String accountHolderName,

        String ifscCode,
        String branchName,

        String addressLine,
        String city,
        String state,
        String pincode,

        Gender gender,
        LocalDate dateOfBirth,
        KycStatus kycStatus

) {}