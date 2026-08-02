package com.siddu.transactionservices.Dto.Response;

import com.siddu.transactionservices.Enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferMoneyResponse(

        String transactionId,

        AccountTransactionParty debitedFrom,

        AccountTransactionParty creditedTo,

        BigDecimal amount,

        TransactionStatus status,
        String failureReason,

        LocalDateTime transactionTime

) {}
