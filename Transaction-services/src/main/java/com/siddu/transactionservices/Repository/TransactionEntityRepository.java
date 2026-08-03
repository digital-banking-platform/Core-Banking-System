package com.siddu.transactionservices.Repository;

import com.siddu.transactionservices.Entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransactionEntityRepository extends JpaRepository<TransactionEntity, UUID> {

    Optional<TransactionEntity> findByIdempotencyKey(UUID idempotencyKey);
    Page<TransactionEntity> findBysourceAccountNumber(String accountNumber, Pageable pageable);
}
