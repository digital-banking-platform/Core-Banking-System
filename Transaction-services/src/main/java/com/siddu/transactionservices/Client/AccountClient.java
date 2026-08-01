package com.siddu.transactionservices.Client;

import com.siddu.dto.transfer.Request.AccountTransferRequest;
import com.siddu.dto.transfer.Response.AccountTransferResponse;
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


}
