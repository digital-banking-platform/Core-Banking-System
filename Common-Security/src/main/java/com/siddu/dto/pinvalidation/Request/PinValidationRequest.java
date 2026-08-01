package com.siddu.dto.pinvalidation.Request;

import java.util.UUID;

public record PinValidationRequest (
        UUID userId,
        String pin

){}
