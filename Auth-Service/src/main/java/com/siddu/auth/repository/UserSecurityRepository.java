package com.siddu.auth.repository;

import com.siddu.auth.entity.UserEntity;
import com.siddu.auth.entity.UserSecurityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserSecurityRepository extends JpaRepository<UserSecurityEntity, UUID> {
}
