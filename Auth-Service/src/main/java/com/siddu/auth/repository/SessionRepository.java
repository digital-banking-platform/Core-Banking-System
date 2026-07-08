package com.siddu.auth.repository;

import com.siddu.auth.entity.SessionEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<SessionEntity, UUID> {
    List<SessionEntity> findByUser_IdAndIsActiveTrue(UUID userId);
    @Modifying
    @Transactional
    @Query("""
    UPDATE SessionEntity s
    SET s.isActive = false, s.updatedAt = CURRENT_TIMESTAMP
    WHERE s.user.id = :userId AND s.isActive = true
""")
    void deactivateAllByUser(@Param("userId") UUID userId);

    Optional<SessionEntity>  findByRefreshTokenHash(String refreshToken);

    void deleteByUser_Id(UUID userId);


}
