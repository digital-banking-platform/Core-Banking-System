package com.siddu.transactionservices.Controller;

import com.siddu.transactionservices.Dto.Requests.AccountNumberRequest;
import com.siddu.transactionservices.Dto.Requests.TransferMoneyRequest;
import com.siddu.transactionservices.Dto.Response.TransferMoneyResponse;
import com.siddu.transactionservices.Services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/history")
    public ResponseEntity<Page<TransferMoneyResponse>> getHistory(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestBody AccountNumberRequest request){
        return ResponseEntity.ok(transactionService.getTransactionshistory(page,size,request));

    }
}

