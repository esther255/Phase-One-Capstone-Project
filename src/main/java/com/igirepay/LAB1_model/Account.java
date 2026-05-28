package com.igirepay.LAB1_model;

import com.igirepay.LAB3_exception.DuplicateTransactionException;
import com.igirepay.LAB3_exception.InsufficientBalanceException;
import com.igirepay.LAB3_exception.InvalidAmountException;
import com.igirepay.LAB3_exception.WithdrawalLimitExceededException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class Account {
    protected int id;
    protected int customerId;
    protected String accountType;
    protected BigDecimal balance;
    protected LocalDateTime createdAt;

    public Account() {}

    public Account(int customerId, String accountType, BigDecimal initialBalance) {
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = initialBalance != null ? initialBalance : BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public void deposit(BigDecimal amount) throws InvalidAmountException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive");
        }
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) throws InvalidAmountException, InsufficientBalanceException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance. Available: " + this.balance);
        }
        this.balance = this.balance.subtract(amount);
    }

    public abstract Transaction processTransaction(Transaction transaction)
            throws InvalidAmountException, InsufficientBalanceException,
            WithdrawalLimitExceededException, DuplicateTransactionException;

    @Override
    public String toString() {
        return String.format("Account{id=%d, type=%s, balance=%s}", id, accountType, balance);
    }
}