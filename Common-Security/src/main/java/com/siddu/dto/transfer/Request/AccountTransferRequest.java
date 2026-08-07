package com.siddu.dto.transfer.Request;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountTransferRequest(

        UUID transactionId,
        String senderAccountNumber,
        String receiverAccountNumber,
        BigDecimal amount,
        String transactionPin

) {}
