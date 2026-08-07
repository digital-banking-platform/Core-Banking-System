package com.siddu.accounts.Dto.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SetPinRequest {

    @NotBlank(message = "account number is required")
    String AccountNumber;

    @NotBlank
    @Pattern(regexp = "[0-9]{4}")

    @NotBlank(message = "PIN is required")
    @Pattern(
            regexp = "^\\d{6}$",
            message = "PIN must contain exactly 6 digits"
    )
    private String pin;

    @NotBlank(message = "Confirm PIN is required")
    @Pattern(
            regexp = "^\\d{6}$",
            message = "Confirm PIN must contain exactly 6 digits"
    )
    private String pinConfirm;
}
