package com.igirepay.LAB3_exception;
import java.util.Scanner;
public class WithdrawalLimitExceededException extends Exception {
    public WithdrawalLimitExceededException(String message) {
        super(message);
    }
}
