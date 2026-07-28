package com.siddu.accounts.Dto.Requests;


import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record GetUserProfileRequest (
        @NotBlank(message = "provide correct Aadhaar number")
        @Length(min = 12, max = 12)
        String AadhaarNumber
){
}
