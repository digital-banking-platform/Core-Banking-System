package com.siddu.transactionservices.Services;


import com.siddu.Enums.ValidationStatus;
import com.siddu.dto.transfer.Response.TransactionValidationResponse;
import com.siddu.transactionservices.Client.AccountClient;
import com.siddu.transactionservices.Dto.Requests.TransferMoneyRequest;
import com.siddu.transactionservices.Dto.Response.TXNFailedResponse;
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

@Service
public class PendingTransactionService {
    private final AccountClient accountClient;
    private final TransactionEntityRepository transactionEntityRepository;

    @Autowired
    public PendingTransactionService(TransactionEntityRepository transactionEntityRepository
    , AccountClient accountClient) {

        this.transactionEntityRepository = transactionEntityRepository;
        this.accountClient = accountClient;


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
            return transactionEntityRepository.save(pendingTransaction);

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


