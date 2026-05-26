package com.igirepay.LAB3_exception;
import java.util.Scanner;
public class InvalidAmountException extends Exception {
    public InvalidAmountException(String message) {
        super(message);
    }
}
