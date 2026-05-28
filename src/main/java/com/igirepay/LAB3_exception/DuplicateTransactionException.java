package com.igirepay.LAB3_exception;

public class DuplicateTransactionException extends Exception {
    public DuplicateTransactionException(String message) {
        super(message);
    }
}