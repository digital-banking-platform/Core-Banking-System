package com.siddu.transactionservices.Exceptions;

import com.siddu.transactionservices.Dto.Response.TransferMoneyResponse;
import lombok.Getter;

@Getter
public class AccessForbiddenException extends RuntimeException {

    private final TransferMoneyResponse moneyResponse;

    public AccessForbiddenException(TransferMoneyResponse moneyResponse) {
        this.moneyResponse = moneyResponse;
    }


}