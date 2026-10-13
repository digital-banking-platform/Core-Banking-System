package com.siddu.transactionservices.Repository;

import com.siddu.transactionservices.Enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TransactionHistoryRow {

    String getTransactionId();

    String getDebitedFromAccountHolderName();

    String getDebitedFromAccountNumber();

    String getCreditedToAccountHolderName();

    String getCreditedToAccountNumber();

    BigDecimal getAmount();

    TransactionStatus getStatus();

    String getFailureReason();

    LocalDateTime getCompletedAt();
}