package com.siddu.transactionservices.Services;


import com.siddu.transactionservices.Dto.Requests.TransferMoneyRequest;
import com.siddu.transactionservices.Entity.TransactionEntity;
import com.siddu.transactionservices.Enums.TransactionStatus;
import com.siddu.transactionservices.Enums.TransactionType;
import com.siddu.transactionservices.Repository.TransactionEntityRepository;
import com.siddu.transactionservices.Utils.TransactionIdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PendingTransactionService {
    private final TransactionEntityRepository transactionEntityRepository;
    public PendingTransactionService(TransactionEntityRepository transactionEntityRepository) {
        this.transactionEntityRepository = transactionEntityRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TransactionEntity createPendingTransaction(TransferMoneyRequest request) {

            TransactionEntity pendingTransaction = TransactionEntity.builder()
                    .idempotencyKey(request.idempotencyKey())
                    .transactionId(TransactionIdGenerator.generate())
                    .sourceAccountNumber(request.senderAccountNumber())
                    .destinationAccountNumber(request.receiverAccountNumber())
                    .transactionType(TransactionType.TRANSFER)
                    .status(TransactionStatus.PENDING)
                    .amount(request.amount())
                    .build();

            return transactionEntityRepository.save(pendingTransaction);

        }

}


