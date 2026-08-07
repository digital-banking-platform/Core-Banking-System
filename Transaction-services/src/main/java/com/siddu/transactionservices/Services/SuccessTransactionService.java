package com.siddu.transactionservices.Services;


import com.siddu.transactionservices.Dto.Response.AccountTransactionParty;
import com.siddu.transactionservices.Dto.Response.TransferMoneyResponse;
import com.siddu.transactionservices.Entity.TransactionEntity;
import com.siddu.transactionservices.Enums.TransactionStatus;
import com.siddu.transactionservices.Repository.TransactionEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SuccessTransactionService {
    private final TransactionEntityRepository transactionEntityRepository;

    SuccessTransactionService(TransactionEntityRepository transactionEntityRepository) {
        this.transactionEntityRepository = transactionEntityRepository;
    }

    @Transactional
    public TransferMoneyResponse saveSuccessTransaction(TransactionEntity transaction){
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setFailureReason(null);
        transaction.setCompletedAt(LocalDateTime.now());
        transactionEntityRepository.save(transaction);

        return new TransferMoneyResponse(
                transaction.getTransactionId(),
                new AccountTransactionParty(
                        transaction.getSnapshot().getSourceAccountHolderName(),
                        transaction.getSourceAccountNumber()
                ),
                new AccountTransactionParty(
                        transaction.getSnapshot().getDestinationAccountHolderName(),
                        transaction.getDestinationAccountNumber()
                ),
                transaction.getAmount(),
                transaction.getStatus(),
                transaction.getFailureReason(),
                transaction.getCompletedAt());
    }
}
