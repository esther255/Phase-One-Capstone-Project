package com.igirepay.LAB1_model;

import com.igirepay.LAB3_exception.*;

import java.math.BigDecimal;
public class SavingsAccount extends Account {
    private int withdrawalLimit;
    private int withdrawalCount;
    private BigDecimal withdrawalFee;

    public SavingsAccount() {
        this.withdrawalLimit = 3;
        this.withdrawalCount = 0;
        this.withdrawalFee = BigDecimal.valueOf(500); // e.g., 500 RWF
    }

    public SavingsAccount(int customerId, BigDecimal initialBalance, int withdrawalLimit, BigDecimal withdrawalFee) {
        super(customerId, "SAVINGS", initialBalance);
        this.withdrawalLimit = withdrawalLimit;
        this.withdrawalCount = 0;
        this.withdrawalFee = withdrawalFee != null ? withdrawalFee : BigDecimal.ZERO;
    }

    public int getWithdrawalLimit() { return withdrawalLimit; }
    public void setWithdrawalLimit(int withdrawalLimit) { this.withdrawalLimit = withdrawalLimit; }

    public int getWithdrawalCount() { return withdrawalCount; }
    public void setWithdrawalCount(int withdrawalCount) { this.withdrawalCount = withdrawalCount; }

    public BigDecimal getWithdrawalFee() { return withdrawalFee; }
    public void setWithdrawalFee(BigDecimal withdrawalFee) { this.withdrawalFee = withdrawalFee; }

    @Override
    public void withdraw(BigDecimal amount) throws InvalidAmountException, InsufficientBalanceException {
        if (withdrawalCount >= withdrawalLimit) {
            throw new InsufficientBalanceException("Withdrawal limit exceeded: only " + withdrawalLimit + " withdrawals allowed per month");
        }
        BigDecimal totalDeduction = amount.add(withdrawalFee);
        if (this.balance.compareTo(totalDeduction) < 0) {
            throw new InsufficientBalanceException("Insufficient balance to cover amount + fee of " + withdrawalFee);
        }
        this.balance = this.balance.subtract(totalDeduction);
        withdrawalCount++;
    }

    @Override
    public Transaction processTransaction(Transaction transaction)
            throws InvalidAmountException, InsufficientBalanceException,
            WithdrawalLimitExceededException, DuplicateTransactionException {
        if (transaction.getType().equalsIgnoreCase("DEPOSIT")) {
            this.deposit(transaction.getAmount());
        } else if (transaction.getType().equalsIgnoreCase("SAVINGS_WITHDRAW")) {
            this.withdraw(transaction.getAmount());
        } else {
            throw new IllegalArgumentException("Unsupported transaction type for Savings: " + transaction.getType());
        }
        return transaction;
    }

    @Override
    public String toString() {
        return String.format("SavingsAccount{id=%d, balance=%s, withdrawalsUsed=%d/%d, fee=%s}",
                id, balance, withdrawalCount, withdrawalLimit, withdrawalFee);
    }
}