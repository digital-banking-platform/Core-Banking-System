package com.siddu.transactionservices.Dto.Response;

import com.siddu.Enums.ValidationStatus;
import com.siddu.dto.transfer.Response.ErrorResponse;

public record TXNFailedResponse(
        ValidationStatus status,
        String message
) implements ErrorResponse {
}
