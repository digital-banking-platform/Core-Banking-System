package com.siddu.accounts.Dto.Responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AccountsResponse {
    List<AccountDetailsResponse>  accountDetailsResponseList;
}
