package com.igirepay.LAB3_exception;
import java.util.Scanner;
public class DuplicateTransactionException extends Exception {
    public DuplicateTransactionException(String message) {
        super(message);
    }
}
