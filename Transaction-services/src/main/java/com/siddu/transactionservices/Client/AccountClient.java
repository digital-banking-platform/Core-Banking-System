package com.siddu.transactionservices.Client;

import com.siddu.dto.account.Request.AccountIdentifier;
import com.siddu.dto.account.Response.AccountIdentifierResponse;
import com.siddu.dto.transfer.Request.AccountTransferRequest;
import com.siddu.dto.transfer.Request.TransactionValidationRequest;
import com.siddu.dto.transfer.Response.AccountTransferResponse;
import com.siddu.dto.transfer.Response.TransactionValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "account-service",
        url = "${services.account.url}"
)
public interface AccountClient {

    @PostMapping("/accounts/internals/transfer")
    AccountTransferResponse transfer(
            @RequestBody AccountTransferRequest request
    );

    @PostMapping("/accounts/internals/transaction-validation")
    TransactionValidationResponse transactionValidation(@RequestBody
                                                        TransactionValidationRequest request);

    @PostMapping("/accounts/internals/check-ownership")
    AccountIdentifierResponse checkOwnership(@RequestBody AccountIdentifier accountIdentifier);


}
