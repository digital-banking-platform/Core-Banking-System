package com.siddu.transactionservices.Utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class TransactionIdGenerator {

    private static final String PREFIX = "TXN";

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private static final SecureRandom RANDOM = new SecureRandom();

    private TransactionIdGenerator() {
    }

    public static String generate() {

        String timestamp = LocalDateTime.now().format(FORMATTER);

        int random = RANDOM.nextInt(1000);

        return PREFIX + timestamp + String.format("%03d", random);
    }
}