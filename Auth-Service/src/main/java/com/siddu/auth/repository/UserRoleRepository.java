package com.siddu.auth.repository;

import com.siddu.auth.entity.SessionEntity;
import com.siddu.auth.entity.UserEntity;
import com.siddu.auth.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UUID> {
    List<UserRoleEntity> findByUser(UserEntity user);

    boolean existsByUser_IdAndRole_Id(UUID userId, UUID roleId);

}
