package com.siddu.accounts.Dto.Requests;

import com.siddu.accounts.Enums.AccountType;
import com.siddu.accounts.Enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CreateBankAccountRequest {

    @NotNull(message = "Please provide an account type")
    private AccountType accountType;

    @NotBlank(message = "Please provide the account holder name")
    @Length(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String accountHolderName;

    @NotNull(message = "Please provide the date of birth")
    private LocalDate dateOfBirth;

    @NotNull(message = "Please provide the gender")
    private Gender gender;

    @NotBlank(message = "Please provide the Aadhaar number")
    @Pattern(regexp = "\\d{12}", message = "Aadhaar number must contain exactly 12 digits")
    private String aadhaarNumber;

    @NotBlank(message = "Please provide the phone number")
    @Pattern(regexp = "\\d{10}", message = "Phone number must contain exactly 10 digits")
    private String phoneNumber;

    @NotBlank(message = "Please provide the address")
    @Length(min = 10, max = 100, message = "Address must be between 10 and 100 characters")
    private String addressLine;

    @NotBlank(message = "Please provide the city")
    @Length(min = 2, max = 50, message = "City must be between 2 and 50 characters")
    private String city;

    @NotBlank(message = "Please provide the state")
    @Length(min = 2, max = 50, message = "State must be between 2 and 50 characters")
    private String state;

    @NotBlank(message = "Please provide the pincode")
    @Pattern(regexp = "\\d{6}", message = "Pincode must contain exactly 6 digits")
    private String pincode;

    @NotNull(message = "Please provide the branch id")
    private UUID branchId;
}