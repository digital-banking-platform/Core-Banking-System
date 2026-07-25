package com.siddu.accounts.Dto.Requests;

import java.util.UUID;

public record PinValidationRequest (
        UUID userId,
        String pin

){}
