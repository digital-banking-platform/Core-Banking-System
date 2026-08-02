package com.siddu.accounts.services;

import com.siddu.Enums.TransferStatus;
import com.siddu.accounts.Client.AuthClient;
import com.siddu.accounts.Dto.Responses.PinValidationResponse;
import com.siddu.accounts.Entity.AccountLedgerEntity;
import com.siddu.accounts.Entity.AccountsEntity;
import com.siddu.accounts.Enums.AccountStatus;
import com.siddu.accounts.Enums.LedgerEntryType;
import com.siddu.accounts.repository.AccountEntityRepository;
import com.siddu.accounts.repository.AccountLedgerEntityRepository;
import com.siddu.dto.pinvalidation.Request.PinValidationRequest;
import com.siddu.dto.transfer.Request.AccountTransferRequest;
import com.siddu.dto.transfer.Response.AccountTransferResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.siddu.Enums.TransferErrorCode.*;

@Service
public class BankTransferService {
    private final AccountEntityRepository accountEntityRepository;
    private final AuthClient authClient;
    private final AccountLedgerEntityRepository accountLedgerEntityRepository;

    public BankTransferService(AccountEntityRepository accountEntityRepository, AuthClient authClient
            , AccountLedgerEntityRepository accountLedgerEntityRepository) {
        this.accountEntityRepository = accountEntityRepository;
        this.authClient = authClient;
        this.accountLedgerEntityRepository = accountLedgerEntityRepository;
    }

    @Transactional
    public AccountTransferResponse transfer(AccountTransferRequest request) {

        Optional<AccountsEntity> senderOptional =
                accountEntityRepository.findByAccountNumber(request.senderAccountNumber());

        if (senderOptional.isEmpty()) {
            return new AccountTransferResponse(
                    TransferStatus.FAILED,
                    SENDER_ACCOUNT_NOT_FOUND,
                    "Sender account does not exist.",
                    null,
                    null
            );
        }

        AccountsEntity sender = senderOptional.get();

        String senderAccountholderName=sender.getProfile().getAccountHolderName();



        if (!sender.getProfile().getUserId().equals(request.userId())) {
            return new AccountTransferResponse(
                    TransferStatus.FAILED,
                    ACCESS_DENIED,
                    "You are not authorized to access this account.",
                    senderAccountholderName,
                    null
            );
        }


        if (sender.getStatus() != AccountStatus.ACTIVE) {
            return new AccountTransferResponse(
                    TransferStatus.FAILED,
                    SENDER_ACCOUNT_INACTIVE,
                    "Sender account is not active.",
                    senderAccountholderName,
                    null
            );
        }


        Optional<AccountsEntity> receiverOptional =
                accountEntityRepository.findByAccountNumber(request.receiverAccountNumber());

        if (receiverOptional.isEmpty()) {
            return new AccountTransferResponse(
                    TransferStatus.FAILED,
                    RECEIVER_ACCOUNT_NOT_FOUND,
                    "Receiver account does not exist.",
                    senderAccountholderName,
                    null
            );
        }

        AccountsEntity receiver = receiverOptional.get();
        String receiverAccountholderName=receiver.getProfile().getAccountHolderName();


        if (receiver.getStatus() != AccountStatus.ACTIVE) {
            return new AccountTransferResponse(
                    TransferStatus.FAILED,
                    RECEIVER_ACCOUNT_INACTIVE,
                    "Receiver account is not active.",
                    senderAccountholderName,
                    receiverAccountholderName
            );
        }


        if (sender.getId().equals(receiver.getId())) {
            return new AccountTransferResponse(
                    TransferStatus.FAILED,
                    SAME_ACCOUNT,
                    "Sender and receiver accounts cannot be the same.",
                    senderAccountholderName,
                    receiverAccountholderName
            );
        }

        if (sender.getBalance().compareTo(request.amount()) < 0) {
            return new AccountTransferResponse(
                    TransferStatus.FAILED,
                    INSUFFICIENT_BALANCE,
                    "Insufficient account balance.",
                    senderAccountholderName,
                    receiverAccountholderName
            );
        }


        PinValidationResponse pinResponse =
                authClient.validatePin(new PinValidationRequest(
                        request.userId(),
                        request.transactionPin()
                ));

        if (!pinResponse.valid()) {
            return new AccountTransferResponse(
                    TransferStatus.FAILED,
                    INVALID_PIN,
                    "Invalid transaction PIN.",
                    senderAccountholderName,
                    receiverAccountholderName
            );
        }



        sender.setBalance(sender.getBalance().subtract(request.amount()));


        receiver.setBalance(receiver.getBalance().add(request.amount()));
        AccountLedgerEntity senderLedger = AccountLedgerEntity.builder()
                .accountId(sender.getId())
                .transactionId(request.transactionId())
                .entryType(LedgerEntryType.DEBIT)
                .amount(request.amount())
                .balanceAfter(sender.getBalance())
                .build();
        AccountLedgerEntity receiverLedger = AccountLedgerEntity.builder()
                .accountId(receiver.getId())
                .transactionId(request.transactionId())
                .entryType(LedgerEntryType.CREDIT)
                .amount(request.amount())
                .balanceAfter(receiver.getBalance())
                .build();

        accountEntityRepository.save(sender);

        accountEntityRepository.save(receiver);

        accountLedgerEntityRepository.saveAll(
                List.of(senderLedger, receiverLedger)
        );



       return  new AccountTransferResponse(
                TransferStatus.SUCCESS,
                SUCCESS,
                "Transfer completed successfully.",
                senderAccountholderName,
                receiverAccountholderName
        );




    }
}
