package com.siddu.transactionservices.Services;

import com.siddu.Enums.TransferStatus;
import com.siddu.dto.transfer.Request.AccountTransferRequest;
import com.siddu.dto.transfer.Response.AccountTransferResponse;
import com.siddu.transactionservices.Client.AccountClient;
import com.siddu.transactionservices.Dto.Requests.TransferMoneyRequest;
import com.siddu.transactionservices.Dto.Response.AccountTransactionParty;
import com.siddu.transactionservices.Dto.Response.TransferMoneyResponse;
import com.siddu.transactionservices.Entity.TransactionEntity;
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



    @Transactional
    public TransferMoneyResponse transferMoney(TransferMoneyRequest request) {

        UUID userId = SecurityUtils.getCurrentUserId();

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


        System.out.println(transaction.getTransactionId());
        System.out.println(transaction.getTransactionId().length());
        if(response.status() == TransferStatus.FAILED){

            handleFailedTransfer(response, transaction);

        }


        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCompletedAt(LocalDateTime.now());

        transaction.setSourceAccountId(
                response.sender().accountId()
        );

        transaction.setDestinationAccountId(
                response.receiver().accountId()
        );


        transactionEntityRepository.save(transaction);


        return new TransferMoneyResponse(
                transaction.getTransactionId(),
                new AccountTransactionParty(
                        response.sender().accountHolderName(),
                        response.sender().AccountNumber()
                ),
                new AccountTransactionParty(
                        response.receiver().accountHolderName(),
                        response.receiver().AccountNumber()
                ),
                transaction.getAmount(),
                TransactionStatus.SUCCESS,
                response.message(),
                transaction.getCompletedAt()
        );
    }

    @Transactional
    private void handleFailedTransfer(
            AccountTransferResponse response,
            TransactionEntity transaction
    ) {

        transaction.setStatus(TransactionStatus.FAILED);
        transaction.setFailureReason(response.message());


        if(response.sender() != null) {
            transaction.setSourceAccountId(
                    response.sender().accountId()
            );
        }


        if(response.receiver() != null) {
            transaction.setDestinationAccountId(
                    response.receiver().accountId()
            );
        }



        transactionEntityRepository.save(transaction);


        switch (response.code()) {

            case "SENDER_ACCOUNT_NOT_FOUND",
                 "RECEIVER_ACCOUNT_NOT_FOUND" ->
                    throw new ResourceNotFoundException(response.message());


            case "ACCESS_DENIED" ->
                    throw new AccessForbiddenException(response.message());


            case "SENDER_ACCOUNT_INACTIVE",
                 "RECEIVER_ACCOUNT_INACTIVE" ->
                    throw new AccountInactiveException(response.message());


            case "INSUFFICIENT_BALANCE" ->
                    throw new InsufficientBalanceException(response.message());


            case "INVALID_PIN" ->
                    throw new InvalidPinException(response.message());


            case "SAME_ACCOUNT" ->
                    throw new BadRequestException(response.message());


            default ->
                    throw new BadRequestException(
                            "Transfer failed: " + response.message()
                    );
        }
    }

    private TransactionEntity createPendingTransaction(TransferMoneyRequest request) {

        return TransactionEntity.builder()
                .idempotencyKey(request.idempotencyKey())
                .transactionId(TransactionIdGenerator.generate())
                .transactionType(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING)
                .amount(request.amount())
                .build();
    }
}




