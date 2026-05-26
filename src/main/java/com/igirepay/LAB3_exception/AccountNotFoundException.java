package com.igirepay.LAB3_exception;
import java.util.Scanner;
public class AccountNotFoundException extends Exception {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
