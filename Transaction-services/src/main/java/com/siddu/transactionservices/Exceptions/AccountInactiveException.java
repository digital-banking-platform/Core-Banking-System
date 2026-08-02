package com.siddu.transactionservices.Exceptions;

import com.siddu.transactionservices.Dto.Response.TransferMoneyResponse;
import lombok.Getter;

@Getter
public class AccountInactiveException extends RuntimeException {
    private final TransferMoneyResponse moneyResponse;
    public AccountInactiveException(TransferMoneyResponse moneyResponse) {

        this.moneyResponse = moneyResponse;
    }
}
