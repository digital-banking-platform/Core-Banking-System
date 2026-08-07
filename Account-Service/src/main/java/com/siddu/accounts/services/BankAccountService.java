package com.siddu.accounts.services;



import com.siddu.accounts.Dto.Requests.CheckBalanceRequest;
import com.siddu.accounts.Dto.Requests.CreateBankAccountRequest;
import com.siddu.accounts.Dto.Requests.VerifyAccountRequest;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
@Service
public class BankAccountService {
    private final AccountEntityRepository accountEntityRepository;
    private final AccountProfileEntityRepository accountProfileEntityRepository;
    private final BranchEntityRepository branchEntityRepository;
    private final PasswordEncoder passwordEncoder;

    public BankAccountService(AccountEntityRepository accountEntityRepository
            , AccountProfileEntityRepository accountProfileEntityRepository,
                              BranchEntityRepository branchEntityRepository,
                              PasswordEncoder passwordEncoder
   ) {
        this.accountEntityRepository = accountEntityRepository;
        this.accountProfileEntityRepository = accountProfileEntityRepository;
        this.branchEntityRepository = branchEntityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ApiResponse<BankAccountResponse> createBankAccount(CreateBankAccountRequest request) {

        AccountProfileEntity profile =accountProfileEntityRepository.findByUserId(SecurityUtils.getCurrentUserId())
                .orElseThrow( () ->
                new ResourceNotFoundException( "Please complete your profile before creating an account."));

        if(!profile.getKycStatus().equals(KycStatus.VERIFIED)){
            throw new BadRequestException( "KYC is not verified. Please visit your nearest" +
                    " bank branch to complete verification.");
        }

        String AccountNumber;
        do {
            AccountNumber = AccountNumberGenerator.generate();
        } while (accountEntityRepository.existsByAccountNumber(AccountNumber));

        BranchEntity branch = branchEntityRepository.findByIfscCode(request.getIfscCode()).orElseThrow(
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
                .transactionPinHash(passwordEncoder.encode(request.getPin()))
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
    public Page<BranchResponse> getBranches( int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());
        Page<BranchEntity> branchpage=branchEntityRepository.findAll(pageable);

        return branchpage.map(branch -> new BranchResponse(
                branch.getBranchName(),branch.getIfscCode(),branch.getActive(),
                        branch.getAddressLine(),branch.getCity(),branch.getPincode()
                ));
    }

    public VerifyAccountResponse verifyreciveraccount(VerifyAccountRequest request) {
        System.out.println("Receiver Account: " + request.getAccountNumber());
        AccountsEntity account=accountEntityRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(()-> new ResourceNotFoundException("account not found."));

        if(!account.getStatus().equals(AccountStatus.ACTIVE)){
            throw new AccountInactiveException("account is not active");
        }

        return new VerifyAccountResponse(account.getAccountNumber(),account.getStatus());

    }

    public VerifyAccountResponse verifysenderaccount(VerifyAccountRequest request) {
        AccountsEntity account=accountEntityRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(
                ()-> new ResourceNotFoundException("account not found."));
        if(!account.getProfile().getUserId().equals(SecurityUtils.getCurrentUserId())) {
            throw new AccessForbiddenException("You are not authorized to access this account");
        }
        if(!account.getStatus().equals(AccountStatus.ACTIVE)){
            throw new AccountInactiveException("account is not active");
        }
        return new VerifyAccountResponse(
                account.getAccountNumber(),
                account.getStatus());
    }


}