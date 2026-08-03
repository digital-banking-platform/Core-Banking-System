package com.siddu.dto.pinvalidation.Response;

import com.siddu.Enums.TransferErrorCode;

public record PinValidationResponse(
        TransferErrorCode status,
        String message
) { }
