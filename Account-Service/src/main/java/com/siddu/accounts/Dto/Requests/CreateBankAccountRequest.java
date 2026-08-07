package com.siddu.accounts.Dto.Requests;

import com.siddu.accounts.Enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class CreateBankAccountRequest {

    @NotNull(message = "Please provide an account type")
    private AccountType accountType;

    @NotBlank(message = "Please provide the branch ifscCode")
    private String IfscCode;

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