package com.siddu.dto.transfer.Response;


import com.siddu.Enums.TransferStatus;

public record AccountTransferResponse(

        TransferStatus status,

        String code,

        String message,

        AccountTransferParty sender,

        AccountTransferParty receiver

) {}
