package com.siddu.transactionservices.Repository;

import com.siddu.transactionservices.Entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TransactionEntityRepository extends JpaRepository<TransactionEntity, UUID> {

    Optional<TransactionEntity> findByIdempotencyKey(UUID idempotencyKey);
    Page<TransactionEntity> findBysourceAccountNumber(String accountNumber, Pageable pageable);


    @Query(
            value = """
        SELECT
            t.transaction_id AS "transactionId",
            s.source_account_holder_name AS "debitedFromAccountHolderName",
            s.source_account_number AS "debitedFromAccountNumber",
            s.destination_account_holder_name AS "creditedToAccountHolderName",
            s.destination_account_number AS "creditedToAccountNumber",
            t.amount AS "amount",
            t.status AS "status",
            t.failure_reason AS "failureReason",
            t.completed_at AS "completedAt"
        FROM transactions t
        JOIN transaction_snapshots s ON s.transaction_id = t.id
        WHERE t.source_account_number = :accountNumber
           OR t.destination_account_number = :accountNumber
        ORDER BY t.created_at DESC, t.id DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM transactions t
        JOIN transaction_snapshots s ON s.transaction_id = t.id
        WHERE t.source_account_number = :accountNumber
           OR t.destination_account_number = :accountNumber
        """,
            nativeQuery = true
    )
    Page<TransactionHistoryRow> findAccountTransactions(
            @Param("accountNumber") String accountNumber,
            Pageable pageable
    );


}
