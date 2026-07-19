package com.siddu.accounts.Entity;

import com.siddu.accounts.Enums.AccountStatus;
import com.siddu.accounts.Enums.AccountType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "accounts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_profile_account_type",
                        columnNames = {"profile_id", "account_type"}
                )
        },
        indexes = {
                @Index(name = "idx_accounts_profile", columnList = "profile_id"),
                @Index(name = "idx_accounts_branch", columnList = "branch_id"),
                @Index(name = "idx_accounts_number", columnList = "account_number")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "profile_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_account_profile")
    )
    private AccountProfileEntity profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "branch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_account_branch")
    )
    private BranchEntity branch;

    @Column(name = "account_number", nullable = false, unique = true, updatable = false, length = 16)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Builder.Default
    @Column(nullable = false, length = 3)
    private String currency = "INR";

    @Builder.Default
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}