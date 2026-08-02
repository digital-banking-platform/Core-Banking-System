package com.siddu.transactionservices.Services;

import com.siddu.Enums.TransferErrorCode;
import com.siddu.Enums.TransferStatus;
import com.siddu.dto.transfer.Request.AccountTransferRequest;
import com.siddu.dto.transfer.Response.AccountTransferResponse;
import com.siddu.transactionservices.Client.AccountClient;
import com.siddu.transactionservices.Dto.Requests.TransferMoneyRequest;
import com.siddu.transactionservices.Dto.Response.AccountTransactionParty;
import com.siddu.transactionservices.Dto.Response.TransferMoneyResponse;
import com.siddu.transactionservices.Entity.TransactionEntity;
import com.siddu.transactionservices.Entity.TransactionSnapshotEntity;
import com.siddu.transactionservices.Enums.TransactionStatus;
import com.siddu.transactionservices.Enums.TransactionType;
import com.siddu.transactionservices.Exceptions.*;
import com.siddu.transactionservices.Repository.TransactionEntityRepository;
import com.siddu.transactionservices.Repository.TransactionSnapshotEntityRepository;
import com.siddu.transactionservices.Utils.SecurityUtils;
import com.siddu.transactionservices.Utils.TransactionIdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {
    private final TransactionEntityRepository transactionEntityRepository;
    private final TransactionSnapshotEntityRepository transactionSnapshotEntityRepository;
    private final AccountClient accountClient;


    public TransactionService(TransactionEntityRepository transactionEntityRepository,
                              TransactionSnapshotEntityRepository transactionSnapshotEntityRepository,
                              AccountClient accountClient) {
        this.transactionEntityRepository = transactionEntityRepository;
        this.transactionSnapshotEntityRepository = transactionSnapshotEntityRepository;
        this.accountClient = accountClient;

    }



    @Transactional(noRollbackFor = {
            ResourceNotFoundException.class,
            AccessForbiddenException.class,
            AccountInactiveException.class,
            InsufficientBalanceException.class,
            InvalidPinException.class,
            BadRequestException.class
    })
    public TransferMoneyResponse transferMoney(TransferMoneyRequest request) {

        UUID userId = SecurityUtils.getCurrentUserId();

        Optional<TransactionEntity> existtransaction = transactionEntityRepository.findByIdempotencyKey(request.idempotencyKey());
        if(existtransaction.isPresent()) {
            TransactionEntity transaction = existtransaction.get();
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
                           transaction.getCompletedAt()
                  );


        }
        System.out.println("after");

        TransactionEntity transaction = createPendingTransaction(request);

        transactionEntityRepository.save(transaction);




        AccountTransferResponse response =
                accountClient.transfer(
                        new AccountTransferRequest(
                                transaction.getId(),
                                userId,
                                request.senderAccountNumber(),
                                request.receiverAccountNumber(),
                                request.amount(),
                                request.transactionPin()
                        )
                );

        TransactionStatus status =response.status()==TransferStatus.FAILED ?
                TransactionStatus.FAILED :TransactionStatus.SUCCESS;

        transaction.setStatus(status);
        transaction.setFailureReason(status==TransactionStatus.FAILED ? response.message(): null);
        transaction.setCompletedAt(LocalDateTime.now());


        TransactionSnapshotEntity snapshot =
                TransactionSnapshotEntity.builder()
                        .transaction(transaction)
                        .sourceAccountNumber(transaction.getSourceAccountNumber())
                        .destinationAccountNumber(transaction.getDestinationAccountNumber())
                        .sourceAccountHolderName(response.senderAccountHolderName())
                        .destinationAccountHolderName(response.receiverAccountHolderName())
                        .build();
        transaction.setSnapshot(snapshot);

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


        if(response.status() == TransferStatus.FAILED){
            handleFailedTransfer(response.errorCode(), moneyResponse);

        }
        return moneyResponse;

    }


    private void handleFailedTransfer(
            TransferErrorCode code,
            TransferMoneyResponse moneyResponse
    ) {

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

            case SAME_ACCOUNT ->
                    throw new BadRequestException(moneyResponse);

            default ->
                    throw new BadRequestException(moneyResponse);
        }
    }


    private TransactionEntity createPendingTransaction(TransferMoneyRequest request) {

        return TransactionEntity.builder()
                .idempotencyKey(request.idempotencyKey())
                .transactionId(TransactionIdGenerator.generate())
                .sourceAccountNumber(request.senderAccountNumber())
                .destinationAccountNumber(request.receiverAccountNumber())
                .transactionType(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING)
                .amount(request.amount())
                .build();
    }
}




