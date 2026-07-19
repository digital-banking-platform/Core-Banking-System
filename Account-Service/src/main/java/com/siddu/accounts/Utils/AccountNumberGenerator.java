package com.siddu.accounts.Utils;

import java.security.SecureRandom;

public final class AccountNumberGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private AccountNumberGenerator() {
    }

    public static String generate() {
        StringBuilder sb = new StringBuilder(12);

        sb.append(RANDOM.nextInt(9) + 1);

        for (int i = 1; i < 12; i++) {
            sb.append(RANDOM.nextInt(10));
        }

        return sb.toString();
    }
}