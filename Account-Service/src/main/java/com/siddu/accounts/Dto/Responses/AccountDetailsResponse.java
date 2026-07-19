package com.siddu.accounts.Dto.Responses;

import com.siddu.accounts.Enums.AccountType;

public record AccountDetailsResponse (
        String AccountHolderName,
        String accountnumber,
        String phonenumber,
        String branch,
        String ifsccode,
        AccountType accountType

){

}
