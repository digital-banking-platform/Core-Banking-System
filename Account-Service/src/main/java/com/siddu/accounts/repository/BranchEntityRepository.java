package com.siddu.accounts.repository;

import com.siddu.accounts.Entity.BranchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BranchEntityRepository extends JpaRepository<BranchEntity, UUID> {

}
