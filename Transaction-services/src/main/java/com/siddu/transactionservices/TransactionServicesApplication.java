package com.siddu.transactionservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
        scanBasePackages = {
                "com.siddu.transactionservices",
                "com.siddu.commonsecurity"
        }
)
@EnableFeignClients
@EnableScheduling
public class TransactionServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionServicesApplication.class, args);
    }

}