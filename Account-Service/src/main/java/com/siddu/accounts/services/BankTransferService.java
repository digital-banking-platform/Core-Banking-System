package com.siddu.accounts.services;

import com.siddu.Enums.TransferStatus;
import com.siddu.Enums.ValidationStatus;
import com.siddu.accounts.Entity.AccountLedgerEntity;
import com.siddu.accounts.Entity.AccountsEntity;
import com.siddu.accounts.Enums.AccountStatus;
import com.siddu.accounts.Enums.LedgerEntryType;
import com.siddu.accounts.repository.AccountEntityRepository;
import com.siddu.accounts.repository.AccountLedgerEntityRepository;
import com.siddu.dto.transfer.Request.AccountTransferRequest;
import com.siddu.dto.transfer.Request.TransactionValidationRequest;
import com.siddu.dto.transfer.Response.AccountTransferResponse;
import com.siddu.dto.transfer.Response.TransactionValidationResponse;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.siddu.Enums.TransferErrorCode.*;

@Service
public class BankTransferService {
    private final AccountEntityRepository accountEntityRepository;
    private final AccountLedgerEntityRepository accountLedgerEntityRepository;
    private final PasswordEncoder passwordEncoder;

    public BankTransferService(AccountEntityRepository accountEntityRepository
            , AccountLedgerEntityRepository accountLedgerEntityRepository,PasswordEncoder passwordEncoder) {
        this.accountEntityRepository = accountEntityRepository;
        this.accountLedgerEntityRepository = accountLedgerEntityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            backoff = @Backoff(delay = 50)
    )
    public AccountTransferResponse transfer(AccountTransferRequest request) {

        AccountsEntity senderAccount =
                accountEntityRepository.findByAccountNumber(request.senderAccountNumber()).orElseThrow();



        AccountsEntity receiverAccount =
                accountEntityRepository.findByAccountNumber(request.receiverAccountNumber()).orElseThrow();

        if(!passwordEncoder.matches(request.transactionPin(),senderAccount.getTransactionPinHash())){
            return  new AccountTransferResponse(
                    TransferStatus.FAILED,
                   INVALID_PIN,
                   "Invalid Pin"
            );


        }


        if (senderAccount.getBalance().compareTo(request.amount()) < 0) {
            return new AccountTransferResponse(
                    TransferStatus.FAILED,
                    INSUFFICIENT_BALANCE,
                    "Insufficient account balance."
            );
        }


        senderAccount.setBalance(senderAccount.getBalance().subtract(request.amount()));

        receiverAccount.setBalance(receiverAccount.getBalance().add(request.amount()));

        AccountLedgerEntity senderLedger = AccountLedgerEntity.builder()
                .accountId(senderAccount.getId())
                .transactionId(request.transactionId())
                .entryType(LedgerEntryType.DEBIT)
                .amount(request.amount())
                .balanceAfter(senderAccount.getBalance())
                .build();

        AccountLedgerEntity receiverLedger = AccountLedgerEntity.builder()
                .accountId(receiverAccount.getId())
                .transactionId(request.transactionId())
                .entryType(LedgerEntryType.CREDIT)
                .amount(request.amount())
                .balanceAfter(receiverAccount.getBalance())
                .build();

        accountLedgerEntityRepository.saveAll(
                List.of(senderLedger, receiverLedger)
        );


       return  new AccountTransferResponse(
                TransferStatus.SUCCESS,
                SUCCESS,
                "Transfer completed successfully."
        );




    }



    @Recover
    public AccountTransferResponse recover(
            ObjectOptimisticLockingFailureException ex,
            AccountTransferRequest request
    ){
        return  new AccountTransferResponse(
                TransferStatus.FAILED,
                CONCURRENT_TRANSACTION,
                "Another transaction modified this account. Please try again."

        );
    }


    public TransactionValidationResponse TransactionValidation(TransactionValidationRequest request) {
        Optional<AccountsEntity> sender=
                accountEntityRepository.findByAccountNumber(request.senderAccountNumber());
        if(sender.isEmpty()) {
            return new TransactionValidationResponse(
                    ValidationStatus.Failed,
                    SENDER_ACCOUNT_NOT_FOUND,
                    "sender account not found",
                    null,
                    null
            );
        }

            AccountsEntity senderAccount = sender.get();
            if (!senderAccount.getProfile().getUserId().equals(request.userId())) {
                return new TransactionValidationResponse(
                        ValidationStatus.Failed,
                        ACCESS_DENIED,
                        "You are not authorized to access this account.",
                        null,
                        null
                );

            }

            if (senderAccount.getStatus() != AccountStatus.ACTIVE) {
                return new TransactionValidationResponse(
                        ValidationStatus.Failed,
                        SENDER_ACCOUNT_INACTIVE,
                        "Sender account is not active.",
                        null,
                        null
                );
            }

            Optional<AccountsEntity> receiver =
                    accountEntityRepository.findByAccountNumber(request.receiverAccountNumber());

            if (receiver.isEmpty()) {
                return new TransactionValidationResponse(
                        ValidationStatus.Failed,
                        RECEIVER_ACCOUNT_NOT_FOUND,
                        "Receiver account not found.",
                        null,
                        null
                );
            }

            AccountsEntity receiverAccount = receiver.get();

        if (receiverAccount.getStatus() != AccountStatus.ACTIVE) {
            return new TransactionValidationResponse(
                    ValidationStatus.Failed,
                    RECEIVER_ACCOUNT_INACTIVE,
                    "Receiver account is not active.",
                    null,
                    null
            );
        }
        if (senderAccount.getId().equals(receiverAccount.getId())) {
            return new TransactionValidationResponse(
                    ValidationStatus.Failed,
                    SAME_ACCOUNT,
                    "Sender and receiver accounts cannot be the same.",
                    null,
                    null
                    );
        }

        return new TransactionValidationResponse(
                ValidationStatus.Success,
                SUCCESS,
                "verified successfully",
                senderAccount.getProfile().getAccountHolderName(),
                receiverAccount.getProfile().getAccountHolderName()
        );

    }
}
