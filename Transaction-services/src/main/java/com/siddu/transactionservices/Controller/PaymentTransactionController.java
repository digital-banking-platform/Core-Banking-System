package com.siddu.transactionservices.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentTransactionController {

    @PostMapping("/payment-transaction/debit")
    public  Void hello(){
        return null;
    }
}

