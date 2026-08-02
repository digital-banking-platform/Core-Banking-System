package com.siddu.transactionservices.Exceptions;

import com.siddu.transactionservices.Dto.Response.TransferMoneyResponse;
import lombok.Getter;

@Getter
public class InsufficientBalanceException extends RuntimeException {
    private final TransferMoneyResponse moneyResponse;

    public InsufficientBalanceException(TransferMoneyResponse moneyResponse) {
        this.moneyResponse = moneyResponse;

    }
}
