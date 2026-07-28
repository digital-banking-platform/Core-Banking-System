package com.siddu.accounts.services;

import com.siddu.accounts.Dto.Requests.CreateBranchRequest;
import com.siddu.accounts.Dto.Requests.ReKycRequest;
import com.siddu.accounts.Dto.Responses.*;
import com.siddu.accounts.Entity.AccountProfileEntity;
import com.siddu.accounts.Entity.AccountsEntity;
import com.siddu.accounts.Entity.BranchEntity;
import com.siddu.accounts.Enums.AccountType;
import com.siddu.accounts.Enums.KycStatus;
import com.siddu.accounts.Exceptions.BadRequestException;
import com.siddu.accounts.Exceptions.DuplicateResourceFoundException;
import com.siddu.accounts.Exceptions.ResourceNotFoundException;
import com.siddu.accounts.Utils.IfscGenerator;
import com.siddu.accounts.repository.AccountEntityRepository;
import com.siddu.accounts.repository.AccountProfileEntityRepository;
import com.siddu.accounts.repository.BranchEntityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
public class AccountManagementService {

    private final BranchEntityRepository branchEntityRepository;

    private final AccountProfileEntityRepository accountProfileEntityRepository;

    private final AccountEntityRepository accountEntityRepository;

    public AccountManagementService(BranchEntityRepository branchEntityRepository
    , AccountProfileEntityRepository accountProfileEntityRepository
            , AccountEntityRepository accountEntityRepository) {
        this.branchEntityRepository = branchEntityRepository;
        this.accountProfileEntityRepository = accountProfileEntityRepository;
        this.accountEntityRepository = accountEntityRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ApiResponse<BranchResponse> createBranch(CreateBranchRequest request) {
        if(branchEntityRepository.existsByBranchName(request.getBranchName())) {
            throw new DuplicateResourceFoundException("Branch with name " + request.getBranchName() + " already exists");
        }

        if(branchEntityRepository.existsByAddressLineAndPincode(request.getBranchAddress(),request.getPincode())) {
            throw new DuplicateResourceFoundException( "A branch already exists at this address.");
        }

        String Ifsc;

        do{
            Ifsc= IfscGenerator.generate();
        }
        while(branchEntityRepository.existsByIfscCode(Ifsc));

        BranchEntity branch= BranchEntity.builder()
                .branchName(request.getBranchName())
                .ifscCode(Ifsc)
                .addressLine(request.getBranchAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .build();

        branchEntityRepository.save(branch);
        return new ApiResponse<>(new BranchResponse(branch.getBranchName(),
                branch.getIfscCode()
                ,branch.getActive(),branch.getAddressLine(),
                branch.getCity(),branch.getPincode()),
                "new bank branch created Successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<ProfileResponse> getProfilesBasedOnKYCStatus(KycStatus kycStatus,int page,int size){
        if (page < 0) {
            throw new IllegalArgumentException("Page cannot be negative");
        }

        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("Invalid page size");
        }

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());

        Page<AccountProfileEntity> profiles=accountProfileEntityRepository.findByKycStatus(kycStatus,pageable);

        return profiles.map(profile ->
                new ProfileResponse(profile.getAccountHolderName(),
                        profile.getDateOfBirth(),
                        profile.getPhoneNumber(),
                        profile.getAddressLine(),
                        profile.getCity(),
                        profile.getState(),
                        profile.getPincode(),
                        profile.getKycStatus()
        ));
    }

    public Page<BankAccountResponse> getaccounts(AccountType accountType, int page, int size){
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());
        Page<AccountsEntity> accountsspage = accountEntityRepository.findByAccountType(accountType,pageable);
        return  accountsspage.map(account -> new BankAccountResponse(
                account.getAccountNumber(),
                account.getAccountType(),
                account.getStatus(),
                account.getProfile().getAccountHolderName(),
                account.getBranch().getIfscCode(),
                account.getBranch().getBranchName(),
                account.getProfile().getAddressLine(),
                account.getProfile().getCity(),
                account.getProfile().getState(),
                account.getProfile().getPincode(),
                account.getProfile().getGender(),
                account.getProfile().getDateOfBirth(),
                account.getProfile().getKycStatus()
        ));

    }

    @Transactional
    public ApiResponse<KycUpdateResponse> VerifyUserKycStatus(ReKycRequest request){

        AccountProfileEntity profile=accountProfileEntityRepository.findById(request.ProfileId()).orElseThrow(
                () -> new ResourceNotFoundException("user not found with id " + request.ProfileId())
        );
        if(profile.getKycStatus()!=KycStatus.PENDING){
            throw new BadRequestException("Only profiles with PENDING KYC can be reviewed.");
        }
        profile.setKycStatus(KycStatus.VERIFIED);
        return new ApiResponse<>(new KycUpdateResponse(profile.getId(),
                profile.getAccountHolderName(),
                profile.getKycStatus(),
                profile.getUpdatedAt()),"KYC verified successfully.");

    }
    @Transactional
    public ApiResponse<KycUpdateResponse> RejectKycStatus(ReKycRequest request){
        AccountProfileEntity profile=accountProfileEntityRepository.findById(request.ProfileId()).orElseThrow(
                () -> new ResourceNotFoundException("user not found with id " + request.ProfileId())
        );
        if(profile.getKycStatus()!=KycStatus.PENDING){
            throw new BadRequestException("Only profiles with PENDING KYC can be reviewed.");
        }
        profile.setKycStatus(KycStatus.REJECTED);
        return new ApiResponse<>(new KycUpdateResponse(profile.getId(),
                profile.getAccountHolderName(),
                profile.getKycStatus(),
                profile.getUpdatedAt()),"KYC update rejected.");
    }
    @Transactional
    public UserProfileResponse getUserProfile(String aadhaarNumber){
        AccountProfileEntity profile=accountProfileEntityRepository.findByAadhaarNumber(aadhaarNumber).orElseThrow(
                () -> new ResourceNotFoundException("user not found with id " + aadhaarNumber)
        );
        return new UserProfileResponse(
                profile.getId(),
                profile.getAccountHolderName(),
                profile.getDateOfBirth(),
                profile.getPhoneNumber(),
                profile.getAddressLine(),
                profile.getCity(),
                profile.getState(),
                profile.getPincode(),
                profile.getKycStatus(),
                profile.getUpdatedAt()
        );
    }


}
