package com.siddu.accounts.Utils;

import java.security.SecureRandom;

public class IfscGenerator {
    private static final String prefix="SIDB";
    private static final SecureRandom random = new SecureRandom();

    public static String generate() {
        int branchCode = random.nextInt(10_000_000); // 0 to 9999999
        return prefix + String.format("%07d", branchCode);
    }
}
