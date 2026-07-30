package com.siddu.transactionservices.Repository;

import com.siddu.transactionservices.Entity.AccountLedgerEntity;
import com.siddu.transactionservices.Entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountLedgerEntityRepository extends JpaRepository<AccountLedgerEntity, UUID> {
}
