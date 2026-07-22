package com.siddu.accounts.Dto.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
@AllArgsConstructor
public class AddressUpdateRequest {
    @NotBlank(message = "address is required")
    @Length(min = 10,max = 100,message = "address characters must be in between 10 and 100" )
    private String address;
    @NotBlank(message = "city name is required")
    @Length(min = 6,max = 16,message = "address characters must be in between 6 and 16" )
    private String city;
    @NotBlank(message = "state name is required")
    @Length(min = 6,max = 16,message = "address characters must be in between 10 and 100" )
    private String state;
    @NotBlank(message = "state name is required")
    @Pattern(regexp = "\\d{6}", message = "Pincode must contain exactly 6 digits")
    private String pincode;
}

