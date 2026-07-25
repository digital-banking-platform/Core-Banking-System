package com.siddu.auth.dto.Requests;

import java.util.UUID;

public record PinValidationRequest(
        UUID userId,
        String pin

)
{
}
