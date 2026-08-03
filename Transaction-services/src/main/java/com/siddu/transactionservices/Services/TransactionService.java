package com.siddu.transactionservices.Services;

import com.siddu.Enums.CheckOwnerShip;
import com.siddu.Enums.TransferStatus;
import com.siddu.dto.account.Request.AccountIdentifier;
import com.siddu.dto.account.Response.AccountIdentifierResponse;
import com.siddu.dto.transfer.Request.AccountTransferRequest;
import com.siddu.dto.transfer.Response.AccountTransferResponse;
import com.siddu.transactionservices.Client.AccountClient;
import com.siddu.transactionservices.Dto.Requests.AccountNumberRequest;
import com.siddu.transactionservices.Dto.Requests.TransferMoneyRequest;
import com.siddu.transactionservices.Dto.Response.AccountTransactionParty;
import com.siddu.transactionservices.Dto.Response.TransferMoneyResponse;
import com.siddu.transactionservices.Entity.TransactionEntity;
import com.siddu.transactionservices.Entity.TransactionSnapshotEntity;
import com.siddu.transactionservices.Enums.PendingReason;
import com.siddu.transactionservices.Enums.TransactionStatus;
import com.siddu.transactionservices.Exceptions.*;
import com.siddu.transactionservices.Repository.TransactionEntityRepository;
import com.siddu.transactionservices.Utils.SecurityUtils;
import feign.FeignException;
import feign.RetryableException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {
    private final TransactionEntityRepository transactionEntityRepository;
    private final AccountClient accountClient;
    private final PendingTransactionService pendingTransactionService;
    private final FailedTransactionService failedTransactionService;


    public TransactionService(TransactionEntityRepository transactionEntityRepository,
                              AccountClient accountClient,
                              PendingTransactionService pendingTransactionService,
                              FailedTransactionService failedTransactionService) {
        this.transactionEntityRepository = transactionEntityRepository;
        this.accountClient = accountClient;
        this.pendingTransactionService = pendingTransactionService;
        this.failedTransactionService = failedTransactionService;

    }



    @Transactional()
    public TransferMoneyResponse transferMoney(TransferMoneyRequest request) {

        UUID userId = SecurityUtils.getCurrentUserId();

        Optional<TransactionEntity> ExistTransaction = transactionEntityRepository.findByIdempotencyKey(request.idempotencyKey());
        if(ExistTransaction.isPresent()) {
            TransactionEntity transaction = ExistTransaction.get();
            String sourceaccountholdername=transaction.getStatus()==TransactionStatus.PENDING ?null:
                    transaction.getSnapshot().getSourceAccountHolderName();
            String destinationaccountholdername=transaction.getStatus()==TransactionStatus.PENDING ?null:
                    transaction.getSnapshot().getDestinationAccountHolderName();
           return new TransferMoneyResponse(
                           transaction.getTransactionId(),
                           new AccountTransactionParty(
                                   sourceaccountholdername,
                                   transaction.getSourceAccountNumber()
                           ),
                           new AccountTransactionParty(
                                   destinationaccountholdername,
                                   transaction.getDestinationAccountNumber()
                           ),
                           transaction.getAmount(),
                           transaction.getStatus(),
                           transaction.getFailureReason(),
                           transaction.getCompletedAt()
                  );


        }

            TransactionEntity transaction = pendingTransactionService.createPendingTransaction(request);
        AccountTransferResponse response;


        try {


             response =
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
        }
        catch (RetryableException ex){

            transaction.setPendingReason(
                    PendingReason.ACCOUNT_SERVICE_UNAVAILABLE
            );

            transactionEntityRepository.save(transaction);

            return pendingResponse(transaction);
        }
        catch (FeignException.InternalServerError |
               FeignException.BadGateway |
               FeignException.ServiceUnavailable |
               FeignException.GatewayTimeout ex){

            transaction.setPendingReason(
                    PendingReason.UNKNOWN_OUTCOME
            );

            transactionEntityRepository.save(transaction);

            return pendingResponse(transaction);
        }
        catch(FeignException ex){

            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setFailureReason("ACCOUNT_SERVICE_ERROR");
            transaction.setCompletedAt(LocalDateTime.now());

            transactionEntityRepository.save(transaction);

            throw ex;
        }


        TransactionSnapshotEntity snapshot =
                TransactionSnapshotEntity.builder()
                        .transaction(transaction)
                        .sourceAccountNumber(transaction.getSourceAccountNumber())
                        .destinationAccountNumber(transaction.getDestinationAccountNumber())
                        .sourceAccountHolderName(response.senderAccountHolderName())
                        .destinationAccountHolderName(response.receiverAccountHolderName())
                        .build();
        transaction.setSnapshot(snapshot);

        if(response.status() == TransferStatus.FAILED){
            failedTransactionService.handleFailedTransaction(response,transaction);

        }
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setFailureReason(null);
        transaction.setCompletedAt(LocalDateTime.now());
        transaction.setSnapshot(snapshot);
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

    private TransferMoneyResponse pendingResponse(TransactionEntity transaction) {
        return new TransferMoneyResponse(
                transaction.getTransactionId(),
                new AccountTransactionParty(null, transaction.getSourceAccountNumber()),
                new AccountTransactionParty(null, transaction.getDestinationAccountNumber()),
                transaction.getAmount(),
                TransactionStatus.PENDING,
                transaction.getFailureReason(),
                null
        );

    }



    public Page<TransferMoneyResponse> getTransactionshistory(int page, int size, AccountNumberRequest request){

        AccountIdentifierResponse Ownership=accountClient.checkOwnership(new AccountIdentifier(
                SecurityUtils.getCurrentUserId(),
                request.accountNumber()));


        if(Ownership.owner().equals(CheckOwnerShip.INVALID_OWNER)){
            System.out.println(Ownership.owner() + " is not owner of this account");
            throw new AccessForbiddenException(null);

        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<TransactionEntity> history=transactionEntityRepository.findBysourceAccountNumber(request.accountNumber(),pageable);

        return history.map(txn ->
                    new TransferMoneyResponse(
                            txn.getTransactionId(),
                            new AccountTransactionParty(
                                    txn.getStatus()==TransactionStatus.PENDING ? null:
                                    txn.getSnapshot().getSourceAccountHolderName(),
                                    txn.getSourceAccountNumber()
                            ),
                            new AccountTransactionParty(
                                    txn.getStatus()==TransactionStatus.PENDING ? null :
                                            txn.getSnapshot().getDestinationAccountHolderName(),
                                    txn.getDestinationAccountNumber()
                            ),
                            txn.getAmount(),
                            txn.getStatus(),
                            txn.getFailureReason(),
                            txn.getCompletedAt()
                    ));

    }
}




