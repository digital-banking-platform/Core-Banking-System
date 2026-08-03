package com.siddu.transactionservices.Dto.Requests;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record AccountNumberRequest(
        @NotBlank(message = "account number is required")
        @Length(min = 12, max = 12)
        String accountNumber
) {
}
