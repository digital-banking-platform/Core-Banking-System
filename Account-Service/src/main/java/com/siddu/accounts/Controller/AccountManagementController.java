package com.siddu.accounts.Controller;


import com.siddu.accounts.Dto.Requests.CreateBranchRequest;
import com.siddu.accounts.Dto.Requests.GetUserProfileRequest;
import com.siddu.accounts.Dto.Requests.ReKycRequest;
import com.siddu.accounts.Dto.Responses.*;
import com.siddu.accounts.Enums.AccountType;
import com.siddu.accounts.Enums.KycStatus;
import com.siddu.accounts.services.AccountManagementService;
import com.siddu.accounts.services.ProfilemanagementService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
public class AccountManagementController {
    private final AccountManagementService accountManagementService;
    private final ProfilemanagementService profilemanagementService;
    public AccountManagementController(AccountManagementService accountManagementService
    , ProfilemanagementService profilemanagementService) {
        this.accountManagementService = accountManagementService;
        this.profilemanagementService = profilemanagementService;

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/branches")
    public ResponseEntity<ApiResponse<BranchResponse>> createBranch(@RequestBody CreateBranchRequest request) {
        return ResponseEntity.ok(accountManagementService.createBranch(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/admin/profiles")
    public ResponseEntity<Page<ProfileResponse>> getProfiles(@RequestParam(defaultValue ="0" ) int page,
                                                             @RequestParam(defaultValue = "6") int size) {
        return ResponseEntity.ok(profilemanagementService.getallprofiles(page,size));
    }
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/admin/accounts")
    public ResponseEntity<Page<BankAccountResponse>> getaccounts(@RequestParam(defaultValue = "SAVINGS")
                                                                     AccountType accountType,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "6") int size) {
        return ResponseEntity.ok(accountManagementService.getaccounts(accountType,page,size));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/admin/profiles/kycstatus")
    public ResponseEntity<Page<ProfileResponse>> getProfilesBasedOnKYCStatus(
            @RequestParam(defaultValue = "PENDING")KycStatus kycStatus
           ,@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        return ResponseEntity.ok(accountManagementService.getProfilesBasedOnKYCStatus(kycStatus,page,size));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/kycstatus/verify")
    public  ResponseEntity<ApiResponse<KycUpdateResponse>> VerifyUserKycStatus(@Valid @RequestBody()
                                                                                   ReKycRequest request){
        return ResponseEntity.ok(accountManagementService.VerifyUserKycStatus(request));

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/kycstatus/reject")
    public ResponseEntity<ApiResponse<KycUpdateResponse>> RejectUserKycStatus(@Valid @RequestBody()
                                                                                  ReKycRequest request){
        return ResponseEntity.ok(accountManagementService.RejectKycStatus(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/admin/kyc/search")
    public ResponseEntity<UserProfileResponse> getUserProfile(@Valid @RequestBody
                                                                  GetUserProfileRequest request){
        return  ResponseEntity.ok(accountManagementService.getUserProfile(request.AadhaarNumber()));

    }


}
