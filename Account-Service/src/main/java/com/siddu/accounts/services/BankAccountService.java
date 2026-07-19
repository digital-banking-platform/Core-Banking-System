package com.siddu.accounts.services;

import com.siddu.accounts.Dto.Requests.CheckBalanceRequest;
import com.siddu.accounts.Dto.Requests.CreateBankAccountRequest;
import com.siddu.accounts.Dto.Responses.*;
import com.siddu.accounts.Entity.AccountProfileEntity;
import com.siddu.accounts.Entity.AccountsEntity;
import com.siddu.accounts.Entity.BranchEntity;
import com.siddu.accounts.Enums.AccountStatus;
import com.siddu.accounts.Enums.KycStatus;
import com.siddu.accounts.Exceptions.*;
import com.siddu.accounts.Utils.AccountNumberGenerator;
import com.siddu.accounts.Utils.SecurityUtils;
import com.siddu.accounts.repository.AccountEntityRepository;
import com.siddu.accounts.repository.AccountProfileEntityRepository;
import com.siddu.accounts.repository.BranchEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
public class BankAccountService {
    private final AccountEntityRepository accountEntityRepository;
    private final AccountProfileEntityRepository accountProfileEntityRepository;
    private final BranchEntityRepository branchEntityRepository;

    public BankAccountService(AccountEntityRepository accountEntityRepository
            , AccountProfileEntityRepository accountProfileEntityRepository, BranchEntityRepository branchEntityRepository) {
        this.accountEntityRepository = accountEntityRepository;
        this.accountProfileEntityRepository = accountProfileEntityRepository;
        this.branchEntityRepository = branchEntityRepository;
    }





    private AccountProfileEntity createProfile(CreateBankAccountRequest request, UUID userId) {

        if (accountProfileEntityRepository.existsByAadhaarNumber(request.getAadhaarNumber())) {
            throw new AccountAlreadyExistsException("Aadhaar Number already exists with other profile ");
        }

        if (request.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new InvalidAgeException("date of birth cannot be in future");
        }

        if (request.getDateOfBirth().isAfter(LocalDate.now().minusYears(18))) {
            throw new InvalidAgeException("User must be At least 18 years old");
        }

        AccountProfileEntity accountProfileEntity = AccountProfileEntity.builder()
                .userId(userId)
                .accountHolderName(request.getAccountHolderName())
                .dateOfBirth(request.getDateOfBirth())
                .aadhaarNumber(request.getAadhaarNumber())
                .gender(request.getGender())
                .addressLine(request.getAddressLine())
                .city(request.getCity())
                .state(request.getState())
                .kycStatus(KycStatus.VERIFIED)
                .phoneNumber(request.getPhoneNumber())
                .pincode(request.getPincode())
                .build();

        return accountProfileEntityRepository.save(accountProfileEntity);


    }


    private void validateExistingProfile(AccountProfileEntity profile, CreateBankAccountRequest request) {
        if (!profile.getAadhaarNumber().equalsIgnoreCase(request.getAadhaarNumber())) {
            throw new KycMismatchException("Aadhaar Number does not match the existing KYC profile");
        }

        if (!profile.getAccountHolderName().equalsIgnoreCase(request.getAccountHolderName())) {
            throw new KycMismatchException("Account holder name does not match the existing KYC profile.");
        }
        if (!profile.getDateOfBirth().equals(request.getDateOfBirth())) {
            throw new KycMismatchException("Date of birth does not match the existing KYC profile.");
        }
        if (!profile.getGender().equals(request.getGender())) {
            throw new KycMismatchException("Gender does not match the existing KYC profile.");
        }

    }


    @Transactional
    public ApiResponse<BankAccountResponse> createBankAccount(CreateBankAccountRequest request, UUID userId) {

        AccountProfileEntity profile;

        Optional<AccountProfileEntity> existingProfile = accountProfileEntityRepository.findByUserId(userId);
        if (existingProfile.isPresent()) {
            profile = existingProfile.get();
            validateExistingProfile(profile, request);
            if (accountEntityRepository.existsByProfileUserIdAndAccountType(userId, request.getAccountType())) {
                throw new AccountAlreadyExistsException("Bank Account already exists with AccountType " + request.getAccountType());
            }

        } else
        {
            profile = createProfile(request, userId);
        }

        String AccountNumber;
        do {
            AccountNumber = AccountNumberGenerator.generate();
        } while (accountEntityRepository.existsByAccountNumber(AccountNumber));

        BranchEntity branch = branchEntityRepository.findById(request.getBranchId()).orElseThrow(
                () -> new ResourceNotFoundException("branch not found")
        );
        if(!branch.getActive()){
            throw new ResourceNotFoundException("branch is not active");
        }


        AccountsEntity account = AccountsEntity.builder()
                .profile(profile)
                .accountNumber(AccountNumber)
                .accountType(request.getAccountType())
                .branch(branch)
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.valueOf(500))
                .build();
        account = accountEntityRepository.save(account);

        BankAccountResponse response = new BankAccountResponse(account.getAccountNumber(), account.getAccountType()
                , account.getStatus(), profile.getAccountHolderName(),
                branch.getIfscCode(), branch.getBranchName()
                , profile.getAddressLine(), profile.getCity(),
                profile.getState(), profile.getPincode(),
                profile.getGender(), profile.getDateOfBirth(),
                profile.getKycStatus());
        return new ApiResponse<>(response, "account created successfully");

    }

    public  ProfileResponse getProfileDetails(UUID userId) throws AccountNotFoundException {
        Optional<AccountProfileEntity> profile = accountProfileEntityRepository.findByUserId(userId);
        if (profile.isEmpty()) {
            throw new AccountNotFoundException("user dont have bank accounts");
        }

        AccountProfileEntity accountprofile=profile.get();

        return new ProfileResponse(accountprofile.getAccountHolderName(),accountprofile.getDateOfBirth()
        ,accountprofile.getPhoneNumber(),accountprofile.getAddressLine(),accountprofile.getCity()
        ,accountprofile.getState(),accountprofile.getPincode(),accountprofile.getKycStatus());
    }


    public AccountsResponse getAccountDetails(UUID userId)  {
        AccountProfileEntity profile = accountProfileEntityRepository.findByUserId(userId).orElseThrow(
                ()-> new ResourceNotFoundException("Account profile not found.")
        );

        List<AccountsEntity> accounts=accountEntityRepository.findByProfile(profile);

        String Phonenumber=profile.getPhoneNumber();
        String accountHolderName=profile.getAccountHolderName();
        List<AccountDetailsResponse> response=accounts.stream().
                map(account ->new AccountDetailsResponse(accountHolderName
                ,account.getAccountNumber(),Phonenumber,account.getBranch().getBranchName(),
                        account.getBranch().getIfscCode(),account.getAccountType())).toList();

        return new AccountsResponse(response);

    }
    public CheckBalanceResponse checkaccountbalance(CheckBalanceRequest request)  {
        AccountsEntity account = accountEntityRepository.findByAccountNumber(request.getAccountnumber()).orElseThrow(
                ()-> new ResourceNotFoundException("account not found.")
        );

        if(!account.getProfile().getUserId().equals(SecurityUtils.getCurrentUserId())) {
            throw new AccessForbiddenException("You are not authorized to access this account");
        }
        if(!account.getStatus().equals(AccountStatus.ACTIVE)){
            throw new AccountInactiveException("account is not active");
        }

        return new CheckBalanceResponse(
                account.getAccountNumber(),
                account.getBalance(),
                "balance check successful");

    }


}