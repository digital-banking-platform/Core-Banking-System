package com.siddu.transactionservices.Services;


import com.siddu.Enums.TransferErrorCode;
import com.siddu.dto.transfer.Response.AccountTransferResponse;
import com.siddu.transactionservices.Dto.Response.AccountTransactionParty;
import com.siddu.transactionservices.Dto.Response.TransferMoneyResponse;
import com.siddu.transactionservices.Entity.TransactionEntity;
import com.siddu.transactionservices.Enums.TransactionStatus;
import com.siddu.transactionservices.Exceptions.*;
import com.siddu.transactionservices.Repository.TransactionEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class FailedTransactionService {
    private final TransactionEntityRepository transactionEntityRepository;

    @Autowired
    public FailedTransactionService(TransactionEntityRepository transactionEntityRepository) {
        this.transactionEntityRepository = transactionEntityRepository;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW,
            noRollbackFor = {
                    ResourceNotFoundException.class,
                    AccessForbiddenException.class,
                    AccountInactiveException.class,
                    InsufficientBalanceException.class,
                    InvalidPinException.class,
                    BadRequestException.class,
                    ConcurrentTransactionException.class
            })
    public void  handleFailedTransaction(AccountTransferResponse response, TransactionEntity transaction){
        transaction.setStatus(TransactionStatus.FAILED);
        transaction.setFailureReason(response.message());
        transaction.setCompletedAt(LocalDateTime.now());
        transactionEntityRepository.save(transaction);

        TransferMoneyResponse moneyResponse=new TransferMoneyResponse(
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
        TransferErrorCode code=response.errorCode();
        switch (code) {

            case SENDER_ACCOUNT_NOT_FOUND,
                 RECEIVER_ACCOUNT_NOT_FOUND ->
                    throw new ResourceNotFoundException(moneyResponse);

            case ACCESS_DENIED ->
                    throw new AccessForbiddenException(moneyResponse);

            case SENDER_ACCOUNT_INACTIVE,
                 RECEIVER_ACCOUNT_INACTIVE ->
                    throw new AccountInactiveException(moneyResponse);

            case INSUFFICIENT_BALANCE ->
                    throw new InsufficientBalanceException(moneyResponse);

            case INVALID_PIN ->
                    throw new InvalidPinException(moneyResponse);

            case SAME_ACCOUNT->
                    throw new BadRequestException(moneyResponse);

            case CONCURRENT_TRANSACTION ->
                    throw new ConcurrentTransactionException(moneyResponse);

            default ->
                    throw new BadRequestException(moneyResponse);
        }

    }
}
