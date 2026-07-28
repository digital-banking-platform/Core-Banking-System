package com.siddu.accounts.services;

import com.siddu.accounts.Dto.Requests.AddressUpdateRequest;
import com.siddu.accounts.Dto.Requests.CreateBankAccountRequest;
import com.siddu.accounts.Dto.Responses.ApiResponse;
import com.siddu.accounts.Dto.Responses.ProfileResponse;
import com.siddu.accounts.Dto.Responses.SuccessResponse;
import com.siddu.accounts.Entity.AccountProfileEntity;
import com.siddu.accounts.Enums.KycStatus;
import com.siddu.accounts.Exceptions.*;
import com.siddu.accounts.Utils.SecurityUtils;
import com.siddu.accounts.repository.AccountProfileEntityRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfilemanagementService {
    private final AccountProfileEntityRepository accountProfileEntityRepository;
    public ProfilemanagementService(AccountProfileEntityRepository accountProfileEntityRepository
    ) {
        this.accountProfileEntityRepository = accountProfileEntityRepository;

    }

    public void validateExistingProfile(AccountProfileEntity profile, CreateBankAccountRequest request) {
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



    public AccountProfileEntity createProfile(CreateBankAccountRequest request, UUID userId) {

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
     profilespage.forEach(p->{
         System.out.println(p.getAccountHolderName());
         System.out.println(p.getAadhaarNumber());
     });
        return  profilespage.map(profile->new ProfileResponse(profile.getAccountHolderName(),
                profile.getDateOfBirth(),
                profile.getPhoneNumber(),profile.getAddressLine(),
                profile.getCity(),profile.getState(),
                profile.getPincode()
                ,profile.getKycStatus()));
    }


    public  ProfileResponse getProfileDetails(UUID userId) {
        Optional<AccountProfileEntity> profile = accountProfileEntityRepository.findByUserId(userId);
        if (profile.isEmpty()) {
            throw new AccountNotFoundException("user dont have bank accounts");
        }

        AccountProfileEntity accountprofile=profile.get();

        return new ProfileResponse(accountprofile.getAccountHolderName(),accountprofile.getDateOfBirth()
                ,accountprofile.getPhoneNumber(),accountprofile.getAddressLine(),accountprofile.getCity()
                ,accountprofile.getState(),accountprofile.getPincode(),accountprofile.getKycStatus());
    }




}
