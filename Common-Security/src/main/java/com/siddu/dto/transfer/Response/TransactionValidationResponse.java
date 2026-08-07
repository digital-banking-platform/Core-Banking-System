package com.siddu.dto.transfer.Response;

import com.siddu.Enums.TransferErrorCode;
import com.siddu.Enums.ValidationStatus;

public record TransactionValidationResponse(
        ValidationStatus status,
        TransferErrorCode ErrorCode,
        String message,
        String senderName,
        String ReceiverName

) implements ErrorResponse {
}
