package com.siddu.transactionservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(
        scanBasePackages = {
                "com.siddu.transactionservices",
                "com.siddu.commonsecurity"
        }
)
@EnableFeignClients
public class TransactionServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionServicesApplication.class, args);
    }

}