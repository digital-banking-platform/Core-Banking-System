package com.siddu.auth.repository;

import com.siddu.auth.entity.UserEntity;
import com.siddu.auth.entity.UserSecurityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserSecurityRepository extends JpaRepository<UserSecurityEntity, UUID> {
    boolean existsByUser(UserEntity user);
    @Query("""
       SELECT s.transactionPinHash
       FROM UserSecurityEntity s
       WHERE s.user.id = :userId
       """)
    Optional<String> findTransactionPinHash(UUID userId);
}
