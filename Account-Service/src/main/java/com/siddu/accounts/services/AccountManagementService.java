package com.siddu.accounts.services;

import com.siddu.accounts.Dto.Requests.CreateBranchRequest;
import com.siddu.accounts.Dto.Responses.ApiResponse;
import com.siddu.accounts.Dto.Responses.BranchResponse;
import com.siddu.accounts.Entity.BranchEntity;
import com.siddu.accounts.Exceptions.DuplicateResourceFoundException;
import com.siddu.accounts.Utils.IfscGenerator;
import com.siddu.accounts.repository.BranchEntityRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountManagementService {
    private final BranchEntityRepository branchEntityRepository;
    public AccountManagementService(BranchEntityRepository branchEntityRepository) {
        this.branchEntityRepository = branchEntityRepository;
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

}
