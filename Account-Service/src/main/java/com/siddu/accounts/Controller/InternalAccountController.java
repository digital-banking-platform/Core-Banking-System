package com.siddu.accounts.Controller;

import com.siddu.accounts.Dto.Requests.VerifyAccountRequest;
import com.siddu.accounts.Dto.Responses.BranchResponse;
import com.siddu.accounts.Dto.Responses.VerifyAccountResponse;
import com.siddu.accounts.services.AccountManagementService;
import com.siddu.accounts.services.BankAccountService;
import com.siddu.accounts.services.BankTransferService;
import com.siddu.dto.account.Request.AccountIdentifier;
import com.siddu.dto.account.Response.AccountIdentifierResponse;
import com.siddu.dto.transfer.Request.AccountTransferRequest;
import com.siddu.dto.transfer.Response.AccountTransferResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class InternalAccountController {
    private final BankAccountService bankAccountService;
    private final BankTransferService bankTransferService;
    private final AccountManagementService accountManagementService;

    @Autowired
    InternalAccountController(BankAccountService bankAccountService
    , BankTransferService bankTransferService,
      AccountManagementService accountManagementService) {
        this.bankAccountService = bankAccountService;
        this.bankTransferService = bankTransferService;
        this.accountManagementService = accountManagementService;
    }

    @GetMapping("accounts/internals/branches")
    public ResponseEntity<Page<BranchResponse>> getBranches(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size) {
        return ResponseEntity.ok(bankAccountService.getBranches(page, size));
    }
    @PostMapping("/accounts/internals/verify-receiver")
    public ResponseEntity<VerifyAccountResponse> verifyreciverAccount(@Valid  @RequestBody VerifyAccountRequest request) {
        return ResponseEntity.ok(bankAccountService.verifyreciveraccount(request));

    }
    @PostMapping("/accounts/internals/verify-sender")
    public ResponseEntity<VerifyAccountResponse> verifysenderAccount(@Valid @RequestBody VerifyAccountRequest request) {
        return ResponseEntity.ok(bankAccountService.verifysenderaccount(request));
    }

    @PostMapping("/accounts/internals/transfer")
    public ResponseEntity<AccountTransferResponse> transfer(@Valid @RequestBody AccountTransferRequest request) {
        return ResponseEntity.ok(bankTransferService.transfer(request));
    }

    @PostMapping("/accounts/internals/check-ownership")
    public ResponseEntity<AccountIdentifierResponse> checkOwnership(@Valid @RequestBody AccountIdentifier request) {
        return ResponseEntity.ok(accountManagementService.checkownerShip(request));

    }


}
