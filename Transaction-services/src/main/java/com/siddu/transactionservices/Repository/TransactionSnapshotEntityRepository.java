package com.siddu.transactionservices.Repository;

import com.siddu.transactionservices.Entity.TransactionSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionSnapshotEntityRepository extends JpaRepository<TransactionSnapshotEntity, UUID> {
}
