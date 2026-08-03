package com.siddu.transactionservices.Exceptions;

import com.siddu.transactionservices.Dto.Response.TransferMoneyResponse;
import lombok.Getter;

@Getter
public class ConcurrentTransactionException extends RuntimeException {

    private final TransferMoneyResponse response;

    public ConcurrentTransactionException(TransferMoneyResponse response) {

        this.response = response;
    }


}