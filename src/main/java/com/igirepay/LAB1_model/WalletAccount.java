package com.igirepay.LAB1_model;

import com.igirepay.LAB3_exception.*;

import java.math.BigDecimal;

public class WalletAccount extends Account {

    public WalletAccount() {}

    public WalletAccount(int customerId, BigDecimal initialBalance) {
        super(customerId, "WALLET", initialBalance);
    }

    @Override
    public void withdraw(BigDecimal amount) throws InvalidAmountException, InsufficientBalanceException {
        super.withdraw(amount); // no extra fees or limits
    }

    @Override
    public Transaction processTransaction(Transaction transaction)
            throws InvalidAmountException, InsufficientBalanceException, DuplicateTransactionException {
        switch (transaction.getType().toUpperCase()) {
            case "DEPOSIT":
                this.deposit(transaction.getAmount());
                break;
            case "WITHDRAW":
            case "SEND_MONEY":
                this.withdraw(transaction.getAmount());
                break;
            default:
                throw new IllegalArgumentException("Unsupported transaction type for Wallet: " + transaction.getType());
        }
        return transaction;
    }

    @Override
    public String toString() {
        return "WalletAccount{" + super.toString() + "}";
    }
}