package com.siddu.accounts.repository;

import com.siddu.accounts.Entity.AccountProfileEntity;
import com.siddu.accounts.Enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountProfileEntityRepository extends JpaRepository<AccountProfileEntity, UUID> {

    Optional<AccountProfileEntity> findByUserId(UUID UserId);
    boolean existsByAadhaarNumber(String AadhaarNumber);
    boolean existsByUserId(UUID userId);



}
