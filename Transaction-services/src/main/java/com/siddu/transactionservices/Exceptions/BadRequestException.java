package com.siddu.transactionservices.Exceptions;

import com.siddu.transactionservices.Dto.Response.TransferMoneyResponse;
import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
    private final TransferMoneyResponse moneyResponse;
    public BadRequestException(TransferMoneyResponse moneyResponse) {
        this.moneyResponse = moneyResponse;
    }
}
