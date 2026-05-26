package com.igirepay.LAB3_exception;
import java.util.Scanner;
public class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
