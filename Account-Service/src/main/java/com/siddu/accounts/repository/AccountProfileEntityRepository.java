package com.siddu.accounts.repository;

import com.siddu.accounts.Entity.AccountProfileEntity;
import com.siddu.accounts.Enums.KycStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountProfileEntityRepository extends JpaRepository<AccountProfileEntity, UUID> {

    Optional<AccountProfileEntity> findByUserId(UUID UserId);
    boolean existsByAadhaarNumber(String AadhaarNumber);
    Page<AccountProfileEntity> findByKycStatus(KycStatus kycStatus, Pageable pageable);
    Optional<AccountProfileEntity> findByAadhaarNumber(String AadhaarNumber);
    boolean existsByUserId(UUID UserId);



}
