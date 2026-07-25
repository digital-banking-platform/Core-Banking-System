package com.siddu.accounts.services;

import com.siddu.accounts.Dto.Requests.AddressUpdateRequest;
import com.siddu.accounts.Dto.Responses.ApiResponse;
import com.siddu.accounts.Dto.Responses.BankAccountResponse;
import com.siddu.accounts.Dto.Responses.ProfileResponse;
import com.siddu.accounts.Dto.Responses.SuccessResponse;
import com.siddu.accounts.Entity.AccountProfileEntity;
import com.siddu.accounts.Entity.AccountsEntity;
import com.siddu.accounts.Enums.AccountType;
import com.siddu.accounts.Enums.KycStatus;
import com.siddu.accounts.Exceptions.DuplicateResourceFoundException;
import com.siddu.accounts.Exceptions.KycMismatchException;
import com.siddu.accounts.Exceptions.ResourceNotFoundException;
import com.siddu.accounts.Utils.SecurityUtils;
import com.siddu.accounts.repository.AccountEntityRepository;
import com.siddu.accounts.repository.AccountProfileEntityRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProfilemanagementService {
    private final AccountProfileEntityRepository accountProfileEntityRepository;
    private final AccountEntityRepository accountEntityRepository;
    public ProfilemanagementService(AccountProfileEntityRepository accountProfileEntityRepository
    , AccountEntityRepository accountEntityRepository) {
        this.accountProfileEntityRepository = accountProfileEntityRepository;
        this.accountEntityRepository = accountEntityRepository;
    }

    @Transactional
    public ApiResponse<ProfileResponse> updateAddress(AddressUpdateRequest request){
           AccountProfileEntity profile = accountProfileEntityRepository.findByUserId(SecurityUtils
                           .getCurrentUserId()).orElseThrow(()->
                   new ResourceNotFoundException("user dont have account profile"));

           if(profile.getKycStatus().equals(KycStatus.PENDING)){
               throw new KycMismatchException("your KYC  profile already in pending state.");

           }

           boolean sameaddress=profile.getAddressLine().equals(request.getAddress()) &&
                   profile.getCity().equals(request.getCity()) &&
                   profile.getState().equals(request.getState()) &&
                   profile.getPincode().equals(request.getPincode());

           if(sameaddress){
               throw new DuplicateResourceFoundException
                       ("New address must be different from the current address.");
           }
           profile.setAddressLine(request.getAddress());
           profile.setCity(request.getCity());
           profile.setState(request.getState());
           profile.setPincode(request.getPincode());
           profile.setKycStatus(KycStatus.PENDING);
           accountProfileEntityRepository.save(profile);
           return new ApiResponse<>(new ProfileResponse(
                   profile.getAccountHolderName(), profile.getDateOfBirth()
                   ,profile.getPhoneNumber(),
                   profile.getAddressLine(),
                   profile.getCity(),
                   profile.getState(),
                   profile.getPincode()
                   ,profile.getKycStatus()
           ),  "Address update request submitted successfully and is pending admin approval.");


    }
    public SuccessResponse updateProfilename(String name){
       AccountProfileEntity profile= accountProfileEntityRepository.findByUserId(SecurityUtils.getCurrentUserId()).orElseThrow(()->
                new DuplicateResourceFoundException("user dont have account profile"));
       if(profile.getKycStatus().equals(KycStatus.PENDING)){
           throw new KycMismatchException("your KYC  profile already in pending state.");
       }
       if(profile.getAccountHolderName().equals(name)){
           throw new DuplicateResourceFoundException("new name must be different from current name");
       }
       profile.setAccountHolderName(name);
       profile.setKycStatus(KycStatus.PENDING);
       accountProfileEntityRepository.save(profile);
       return new SuccessResponse("new account holder name "+ profile.getAccountHolderName()
               + " updated successfully. and is pending admin approval.");

    }
    public Page<ProfileResponse> getallprofiles(int page,int size){
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());

        Page<AccountProfileEntity> profilespage = accountProfileEntityRepository.findAll(pageable);
        return  profilespage.map(profile->new ProfileResponse(profile.getAccountHolderName(),
                profile.getDateOfBirth(),
                profile.getPhoneNumber(),profile.getAddressLine(),
                profile.getCity(),profile.getState(),
                profile.getPincode()
                ,profile.getKycStatus()));
    }
    public Page<BankAccountResponse> getaccounts(AccountType accountType,int page,int size){
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



}
