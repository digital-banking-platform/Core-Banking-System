package com.siddu.dto.transfer.Response;


import com.siddu.Enums.TransferErrorCode;
import com.siddu.Enums.TransferStatus;

public record AccountTransferResponse(

        TransferStatus status,

        TransferErrorCode errorCode,

        String message

) {}
