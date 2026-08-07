package com.siddu.dto.transfer.Request;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionValidationRequest(

        UUID userId,

        String senderAccountNumber,

        String receiverAccountNumber
) {
}
