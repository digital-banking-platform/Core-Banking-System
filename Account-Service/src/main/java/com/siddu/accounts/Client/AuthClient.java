package com.siddu.accounts.Client;

import com.siddu.dto.pinvalidation.Request.PinValidationRequest;
import com.siddu.dto.pinvalidation.Response.PinValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "auth-service",
        url = "${services.auth.url}"
)
public interface AuthClient {

    @PostMapping("/internal/pin/validate")
    PinValidationResponse validatePin(@RequestBody PinValidationRequest pinValidationRequest);

}


