package com.siddu.accounts.repository;

import com.siddu.accounts.Entity.AccountProfileEntity;
import com.siddu.accounts.Entity.AccountsEntity;
import com.siddu.accounts.Enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountEntityRepository extends JpaRepository<AccountsEntity, UUID> {

    boolean existsByProfileUserIdAndAccountType(UUID userId, AccountType accountType);
    boolean existsByAccountNumber(String accountNumber);
    Optional<AccountsEntity> findByAccountNumber(String accountNumber);

    List<AccountsEntity> findByProfile(AccountProfileEntity profile);


}
