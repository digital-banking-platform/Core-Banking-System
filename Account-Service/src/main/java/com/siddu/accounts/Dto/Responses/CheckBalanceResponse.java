package com.siddu.accounts.Dto.Responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CheckBalanceResponse {
    String accountnumber;
    BigDecimal balance;
    String message;


}
