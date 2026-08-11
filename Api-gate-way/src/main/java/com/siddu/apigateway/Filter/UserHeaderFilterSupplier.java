package com.siddu.apigateway.Filter;

import org.springframework.cloud.gateway.server.mvc.filter.SimpleFilterSupplier;
import org.springframework.stereotype.Component;

@Component
public class UserHeaderFilterSupplier extends SimpleFilterSupplier {
    public UserHeaderFilterSupplier() {
        super(UserHeaderFilter.class);
    }
}
