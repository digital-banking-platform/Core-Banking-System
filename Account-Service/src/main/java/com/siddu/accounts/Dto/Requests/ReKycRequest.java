package com.siddu.accounts.Dto.Requests;

import com.siddu.accounts.Enums.KycStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReKycRequest(

        @NotNull(message = "profile is required")
        UUID ProfileId
) {

}
