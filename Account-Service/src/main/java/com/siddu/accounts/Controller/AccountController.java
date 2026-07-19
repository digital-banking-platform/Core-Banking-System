package com.siddu.accounts.Controller;

import com.siddu.accounts.Dto.Requests.CheckBalanceRequest;
import com.siddu.accounts.Dto.Requests.CreateBankAccountRequest;
import com.siddu.accounts.Dto.Responses.*;
import com.siddu.accounts.Utils.SecurityUtils;
import com.siddu.accounts.services.BankAccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class AccountController {


    private final BankAccountService bankAccountService;
    @Autowired
    public AccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;

    }

    @PostMapping("accounts/create-bank-account")
    public ResponseEntity<ApiResponse<BankAccountResponse>> createBankAccount(@Valid @RequestBody CreateBankAccountRequest Request) {
        UUID userId= SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(bankAccountService.createBankAccount(Request,userId));

    }

    @GetMapping("Profile/me")
    public ResponseEntity<ProfileResponse> getProfile() {
        UUID userId= SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(bankAccountService.getProfileDetails(userId));
    }
    @GetMapping("/accounts/me")
    public ResponseEntity<AccountsResponse> getAccountProfile() {
        UUID userId= SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(bankAccountService.getAccountDetails(userId));
    }


    @GetMapping("/accounts/check-balance")
    public ResponseEntity<CheckBalanceResponse> getAccountBalance(@RequestBody CheckBalanceRequest request)  {
        return ResponseEntity.ok(bankAccountService.checkaccountbalance(request));



    }
}
