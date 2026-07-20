package com.siddu.auth.dto.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
@AllArgsConstructor
public class SetPinRequest {

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
