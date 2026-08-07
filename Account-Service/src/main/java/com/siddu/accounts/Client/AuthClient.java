package com.siddu.accounts.Client;

import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(
        name = "auth-service",
        url = "${services.auth.url}"
)
public interface AuthClient {


}

