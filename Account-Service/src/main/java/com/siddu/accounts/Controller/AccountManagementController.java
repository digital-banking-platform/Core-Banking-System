package com.siddu.accounts.Controller;


import com.siddu.accounts.Dto.Requests.CreateBranchRequest;
import com.siddu.accounts.Dto.Responses.ApiResponse;
import com.siddu.accounts.Dto.Responses.BankAccountResponse;
import com.siddu.accounts.Dto.Responses.BranchResponse;
import com.siddu.accounts.Dto.Responses.ProfileResponse;
import com.siddu.accounts.Enums.AccountType;
import com.siddu.accounts.Enums.KycStatus;
import com.siddu.accounts.services.AccountManagementService;
import com.siddu.accounts.services.BankAccountService;
import com.siddu.accounts.services.ProfilemanagementService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountManagementController {
    private final AccountManagementService accountManagementService;
    private final ProfilemanagementService profilemanagementService;
    private final BankAccountService bankAccountService;
    public AccountManagementController(AccountManagementService accountManagementService
    , ProfilemanagementService profilemanagementService, BankAccountService bankAccountService) {
        this.accountManagementService = accountManagementService;
        this.profilemanagementService = profilemanagementService;
        this.bankAccountService = bankAccountService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/branches")
    public ResponseEntity<ApiResponse<BranchResponse>> createBranch(@RequestBody CreateBranchRequest request) {
        return ResponseEntity.ok(accountManagementService.createBranch(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/admin/profiles")
    public ResponseEntity<Page<ProfileResponse>> getProfiles(@RequestParam(defaultValue ="0" ) int page, @RequestParam(defaultValue = "6") int size) {
        return ResponseEntity.ok(profilemanagementService.getallprofiles(page,size));
    }
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/admin/accounts")
    public ResponseEntity<Page<BankAccountResponse>> getaccounts(@RequestParam(defaultValue = "SAVINGS")AccountType accountType,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "6") int size) {
        return ResponseEntity.ok(accountManagementService.getaccounts(accountType,page,size));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/profiles/kycstatus")
    public ResponseEntity<Page<ProfileResponse>> getProfilesBasedOnKYCStatus(
            @RequestParam(defaultValue = "PENDING")KycStatus kycStatus
           ,@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        return ResponseEntity.ok(accountManagementService.getProfilesBasedOnKYCStatus(kycStatus,page,size));




    }




}
