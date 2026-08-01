package com.siddu.accounts.repository;

import com.siddu.accounts.Entity.AccountLedgerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountLedgerEntityRepository extends JpaRepository<AccountLedgerEntity, UUID> {
}
