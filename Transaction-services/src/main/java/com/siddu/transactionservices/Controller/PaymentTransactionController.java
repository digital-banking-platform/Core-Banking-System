package com.siddu.transactionservices.Controller;

import com.siddu.transactionservices.Dto.Requests.TransferMoneyRequest;
import com.siddu.transactionservices.Dto.Response.TransferMoneyResponse;
import com.siddu.transactionservices.Services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment-transactions")
public class PaymentTransactionController {
    private final TransactionService transactionService;

    @Autowired
    public PaymentTransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferMoneyResponse> transferMoney(@Valid  @RequestBody TransferMoneyRequest request){
        return ResponseEntity.ok(transactionService.transferMoney(request));
    }
}

