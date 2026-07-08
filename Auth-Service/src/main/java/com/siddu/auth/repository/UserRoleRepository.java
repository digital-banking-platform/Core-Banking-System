package com.siddu.auth.repository;

import com.siddu.auth.entity.SessionEntity;
import com.siddu.auth.entity.UserEntity;
import com.siddu.auth.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UUID> {
    List<UserRoleEntity> findByUser(UserEntity user);

    List<UserRoleEntity> findByUser_IdIn(List<UUID> userIds);
    boolean existsByUserIdAndRoleId(UUID userId, long roleId);

    @Transactional
    @Modifying
    int deleteByUserIdAndRoleId(UUID userId, long roleId);
    @Query("""
        select ur.role.name
        from UserRoleEntity ur
        where ur.user.id = :userId
    """)
    List<String> findRoleNamesByUserId(@Param("userId") UUID userId);


}
