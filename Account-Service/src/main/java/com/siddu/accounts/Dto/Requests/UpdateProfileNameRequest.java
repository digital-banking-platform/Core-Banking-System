package com.siddu.accounts.Dto.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileNameRequest(

        @NotBlank(message = "Name cannot be blank")
        @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
        String name
) {}
