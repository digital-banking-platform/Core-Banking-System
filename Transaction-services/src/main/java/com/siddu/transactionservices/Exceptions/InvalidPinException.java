package com.siddu.transactionservices.Exceptions;

import com.siddu.transactionservices.Dto.Response.TransferMoneyResponse;
import lombok.Getter;

@Getter
public class InvalidPinException extends RuntimeException {
    private final TransferMoneyResponse moneyResponse;

    public InvalidPinException(TransferMoneyResponse moneyResponse) {
        this.moneyResponse = moneyResponse;
    }
}
