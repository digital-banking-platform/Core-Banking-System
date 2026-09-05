package com.siddu.transactionservices.Services;


import com.siddu.Enums.ValidationStatus;
import com.siddu.dto.transfer.Response.TransactionValidationResponse;
import com.siddu.transactionservices.Dto.Requests.TransferMoneyRequest;
import com.siddu.transactionservices.Dto.Response.AccessForbiddenResponse;
import com.siddu.transactionservices.Dto.Response.AccountTransactionParty;
import com.siddu.transactionservices.Dto.Response.TXNFailedResponse;
import com.siddu.transactionservices.Dto.Response.TransferMoneyResponse;
import com.siddu.transactionservices.Entity.TransactionEntity;
import com.siddu.transactionservices.Entity.TransactionSnapshotEntity;
import com.siddu.transactionservices.Enums.PendingReason;
import com.siddu.transactionservices.Enums.TransactionStatus;
import com.siddu.transactionservices.Enums.TransactionType;
import com.siddu.transactionservices.Exceptions.*;
import com.siddu.transactionservices.Repository.TransactionEntityRepository;
import com.siddu.transactionservices.Utils.TransactionIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PendingTransactionService {
    private final TransactionEntityRepository transactionEntityRepository;

    @Autowired
    public PendingTransactionService(TransactionEntityRepository transactionEntityRepository) {

        this.transactionEntityRepository = transactionEntityRepository;


    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TransactionEntity savePendingTransaction(TransferMoneyRequest request,TransactionValidationResponse validationResponse) {
            if(validationResponse.status().equals(ValidationStatus.Failed)){
                handlestatusResponse(validationResponse);
            }

            TransactionSnapshotEntity snapshot= TransactionSnapshotEntity.builder()
                    .sourceAccountNumber(request.senderAccountNumber())
                    .destinationAccountNumber(request.receiverAccountNumber())
                    .sourceAccountHolderName(validationResponse.senderName())
                    .destinationAccountHolderName(validationResponse.ReceiverName())
                    .build();

            TransactionEntity pendingTransaction = TransactionEntity.builder()
                    .idempotencyKey(request.idempotencyKey())
                    .transactionId(TransactionIdGenerator.generate())
                    .sourceAccountNumber(request.senderAccountNumber())
                    .destinationAccountNumber(request.receiverAccountNumber())
                    .transactionType(TransactionType.TRANSFER)
                    .status(TransactionStatus.PENDING)
                    .amount(request.amount())
                    .build();

            snapshot.setTransaction(pendingTransaction);
            pendingTransaction.setSnapshot(snapshot);
            return  transactionEntityRepository.save(pendingTransaction);

        }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TransferMoneyResponse fetchExistingTransaction(String senderAccountNumber, UUID idempotencyKey) {
        TransactionEntity existing = transactionEntityRepository.findByIdempotencyKey(idempotencyKey)
                .orElseThrow();

        boolean isOwner = existing.getSourceAccountNumber().equals(senderAccountNumber);
        if (!isOwner) {
            throw new AccessForbiddenException(new AccessForbiddenResponse("you are not authorized to access transaction"));
        }

        return new TransferMoneyResponse(
                existing.getTransactionId(),
                new AccountTransactionParty(
                        existing.getSnapshot().getSourceAccountHolderName(),
                        existing.getSourceAccountNumber()
                ),
                new AccountTransactionParty(
                        existing.getSnapshot().getDestinationAccountHolderName(),
                        existing.getDestinationAccountNumber()
                ),
                existing.getAmount(),
                existing.getStatus(),
                existing.getFailureReason(),
                existing.getCreatedAt()
        );
    }


        private  void handlestatusResponse(TransactionValidationResponse validationResponse){
            TXNFailedResponse response=new TXNFailedResponse(ValidationStatus.Failed,validationResponse.message());

        switch (validationResponse.ErrorCode()){
            case SENDER_ACCOUNT_NOT_FOUND,
                 RECEIVER_ACCOUNT_NOT_FOUND ->
                    throw new ResourceNotFoundException(response);

            case ACCESS_DENIED ->
                    throw new AccessForbiddenException(response);

            case SENDER_ACCOUNT_INACTIVE,
                 RECEIVER_ACCOUNT_INACTIVE ->
                    throw new AccountInactiveException(response);

            case SAME_ACCOUNT->
                    throw new BadRequestException(response);


            default ->
                    throw new BadRequestException(response);
        }
        }

        @Transactional
        public void markAccountServiceUnavailable(TransactionEntity transaction){
            transaction.setPendingReason(PendingReason.ACCOUNT_SERVICE_UNAVAILABLE);
            transactionEntityRepository.save(transaction);
        }

        @Transactional
        public void markUnknownOutcome(TransactionEntity transaction){
        transaction.setPendingReason(PendingReason.UNKNOWN_OUTCOME);
        transactionEntityRepository.save(transaction);

        }



}


