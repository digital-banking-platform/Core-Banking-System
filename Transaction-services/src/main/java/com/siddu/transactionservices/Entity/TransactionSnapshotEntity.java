package com.siddu.transactionservices.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "transaction_snapshots",
        indexes = {
                @Index(
                        name = "idx_transaction_snapshot_transaction",
                        columnList = "transaction_id"
                )
        }
)
public class TransactionSnapshotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "transaction_id",
            nullable = false,
            unique = true,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_transaction_snapshot_transaction")
    )
    private TransactionEntity transaction;

    @Column(name = "source_account_number", nullable = false, updatable = false, length = 16)
    private String sourceAccountNumber;

    @Column(name = "destination_account_number", nullable = false, updatable = false, length = 16)
    private String destinationAccountNumber;

    @Column(name = "source_account_holder_name", nullable = false, updatable = false, length = 100)
    private String sourceAccountHolderName;

    @Column(name = "destination_account_holder_name", nullable = false, updatable = false, length = 100)
    private String destinationAccountHolderName;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}