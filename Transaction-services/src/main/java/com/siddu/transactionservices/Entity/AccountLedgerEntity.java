package com.siddu.transactionservices.Entity;


import com.siddu.transactionservices.Enums.LedgerEntryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "account_ledger",
        indexes = {

                @Index(
                        name = "idx_ledger_account",
                        columnList = "account_id"
                ),

                @Index(
                        name = "idx_ledger_transaction",
                        columnList = "transaction_id"
                ),

                @Index(
                        name = "idx_ledger_account_created",
                        columnList = "account_id, created_at"
                )
        }
)
public class AccountLedgerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "account_id", nullable = false, updatable = false)
    private UUID accountId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "transaction_id",
            nullable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_ledger_transaction")
    )
    private TransactionEntity transaction;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false, updatable = false)
    private LedgerEntryType entryType;

    @Column(nullable = false, precision = 18, scale = 2, updatable = false)
    private BigDecimal amount;

    @Column(name = "balance_after", nullable = false, precision = 18, scale = 2, updatable = false)
    private BigDecimal balanceAfter;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}