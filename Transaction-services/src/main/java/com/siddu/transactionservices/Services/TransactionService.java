package com.siddu.transactionservices.Services;

import com.siddu.Enums.CheckOwnerShip;
import com.siddu.Enums.TransferStatus;
import com.siddu.dto.account.Request.AccountIdentifier;
import com.siddu.dto.account.Response.AccountIdentifierResponse;
import com.siddu.dto.transfer.Request.AccountTransferRequest;
import com.siddu.dto.transfer.Request.TransactionValidationRequest;
import com.siddu.dto.transfer.Response.AccountTransferResponse;
import com.siddu.dto.transfer.Response.TransactionValidationResponse;
import com.siddu.transactionservices.Client.AccountClient;
import com.siddu.transactionservices.Dto.Requests.AccountNumberRequest;
import com.siddu.transactionservices.Dto.Requests.TransferMoneyRequest;
import com.siddu.transactionservices.Dto.Response.AccountTransactionParty;
import com.siddu.transactionservices.Dto.Response.TransferMoneyResponse;
import com.siddu.transactionservices.Entity.TransactionEntity;
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
import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionEntityRepository transactionEntityRepository;
    private final AccountClient accountClient;
    private final PendingTransactionService pendingTransactionService;
    private final FailedTransactionService failedTransactionService;
    private final SuccessTransactionService successTransactionService;


    public TransactionService(TransactionEntityRepository transactionEntityRepository,
                              AccountClient accountClient,
                              PendingTransactionService pendingTransactionService,
                              FailedTransactionService failedTransactionService,
                              SuccessTransactionService successTransactionService) {
        this.transactionEntityRepository = transactionEntityRepository;
        this.accountClient = accountClient;
        this.pendingTransactionService = pendingTransactionService;
        this.failedTransactionService = failedTransactionService;
        this.successTransactionService = successTransactionService;

    }



    public TransferMoneyResponse transferMoney(TransferMoneyRequest request) {

        Optional<TransactionEntity> ExistTransaction = transactionEntityRepository.findByIdempotencyKey(request.idempotencyKey());
        if(ExistTransaction.isPresent()) {
            TransactionEntity transaction = ExistTransaction.get();
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

        TransactionValidationResponse validationResponse=accountClient.transactionValidation(
                new TransactionValidationRequest(
                        SecurityUtils.getCurrentUserId(),
                        request.senderAccountNumber(),
                        request.receiverAccountNumber()
                ));

        TransactionEntity transaction=pendingTransactionService.savePendingTransaction(request,validationResponse);

        AccountTransferResponse response;

        try {


             response =
                    accountClient.transfer(
                            new AccountTransferRequest(
                                    transaction.getId(),
                                    request.senderAccountNumber(),
                                    request.receiverAccountNumber(),
                                    request.amount(),
                                    request.transactionPin()
                            )
                    );
        }
        catch (RetryableException ex){
            pendingTransactionService.markAccountServiceUnavailable(transaction);
            return pendingResponse(transaction);
        }
        catch (FeignException.InternalServerError |
               FeignException.BadGateway |
               FeignException.ServiceUnavailable |
               FeignException.GatewayTimeout ex){

           pendingTransactionService.markUnknownOutcome(transaction);
            return pendingResponse(transaction);
        }

        catch(FeignException ex){
          failedTransactionService.markAccountServiceError(transaction);
            throw ex;
        }


        if(response.status() == TransferStatus.FAILED){
            failedTransactionService.handleFailedTransaction(response,transaction);
        }

        return successTransactionService.saveSuccessTransaction(transaction);

    }

    private TransferMoneyResponse pendingResponse(TransactionEntity transaction) {
        return new TransferMoneyResponse(
                transaction.getTransactionId(),
                new AccountTransactionParty(transaction.getSnapshot().getSourceAccountHolderName(),
                        transaction.getSourceAccountNumber()),
                new AccountTransactionParty( transaction.getSnapshot().getDestinationAccountHolderName()
                        ,transaction.getDestinationAccountNumber()),
                transaction.getAmount(),
                TransactionStatus.PENDING,
                transaction.getFailureReason(),
                transaction.getCreatedAt()
        );


    }



    public Page<TransferMoneyResponse> getTransactionshistory(int page, int size, AccountNumberRequest request){

        AccountIdentifierResponse Ownership=accountClient.checkOwnership(new AccountIdentifier(
                SecurityUtils.getCurrentUserId(),
                request.accountNumber()));


        if(Ownership.owner().equals(CheckOwnerShip.INVALID_OWNER)){
            throw new AccessForbiddenException(null);

        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<TransactionEntity> history=transactionEntityRepository.findBysourceAccountNumber(request.accountNumber(),pageable);

        return history.map(txn ->
                    new TransferMoneyResponse(
                            txn.getTransactionId(),
                            new AccountTransactionParty(
                                    txn.getSnapshot().getSourceAccountHolderName(),
                                    txn.getSourceAccountNumber()
                            ),
                            new AccountTransactionParty(
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




