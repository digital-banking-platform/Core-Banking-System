package com.siddu.transactionservices.Entity;



import com.siddu.transactionservices.Enums.PendingReason;
import com.siddu.transactionservices.Enums.TransactionStatus;
import com.siddu.transactionservices.Enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(
        name = "transactions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_transactions_transaction_id",
                        columnNames = "transaction_id"
                ),
                @UniqueConstraint(
                        name = "uk_transactions_idempotency_key",
                        columnNames = "idempotency_key"
                )
        },
        indexes = {

                @Index(
                        name = "idx_transactions_destination_account",
                        columnList = "destination_account_number"
                ),

                @Index(
                        name = "idx_transactions_status",
                        columnList = "status"
                ),

                @Index(
                        name = "idx_transactions_created_at",
                        columnList = "created_at"
                ),

                @Index(
                        name = "idx_transactions_status_created",
                        columnList = "status, created_at"
                ),
                @Index(
                        name = "idx_idempotencyKey",
                        columnList = "idempotency_key"
                )
        }
)
@Builder
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "transaction_id", nullable = false, length = 30, updatable = false)
    private String transactionId;

    @Column(name = "idempotency_key", nullable = false,unique = true,updatable = false)
    private UUID idempotencyKey;

    @Column(name = "source_account_number",updatable = false)
    @Length(min=12, max=12,message = "account number must be 12 digits")
    private String sourceAccountNumber;

    @Column(name = "destination_account_number",updatable = false)
    @Length(min=12, max=12,message = "account number must be 12 digits")
    private String destinationAccountNumber;

    @Column(nullable = false, length = 3)
    @Builder.Default
    private String currency = "INR";

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false,updatable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "pending_reason")
    private PendingReason pendingReason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToOne(
            mappedBy = "transaction",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private TransactionSnapshotEntity snapshot;
}
