package com.siddu.transactionservices.Scheduler;


import com.siddu.transactionservices.Repository.TransactionEntityRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TransactionScheduler {
    private final TransactionEntityRepository transactionRepository;
    public TransactionScheduler(TransactionEntityRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


    @Scheduled(fixedRate = 60000)
    public void processPendingTransactions() {

    }

}
