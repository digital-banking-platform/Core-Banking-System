package com.siddu.auth.repository;

import com.siddu.auth.Enums.RoleName;
import com.siddu.auth.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {
    Optional<RoleEntity> findByName(RoleName name);

    boolean existsByName(RoleName name);



}
