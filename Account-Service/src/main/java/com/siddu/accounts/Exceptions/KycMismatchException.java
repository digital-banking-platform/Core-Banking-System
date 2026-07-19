package com.siddu.accounts.Exceptions;

public class KycMismatchException extends RuntimeException {
    public KycMismatchException(String message) {
        super(message);
    }
}
