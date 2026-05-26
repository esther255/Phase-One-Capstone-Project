package com.igirepay.LAB1_service;

import com.igirepay.LAB2_dao.AccountDAO;
import com.igirepay.LAB3_exception.AccountNotFoundException;
import com.igirepay.LAB1_model.Account;
import com.igirepay.LAB1_model.SavingsAccount;
import com.igirepay.LAB1_model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public class AccountService {
    private final AccountDAO accountDAO;
    private final TransactionService transactionService;

    public AccountService(AccountDAO accountDAO, TransactionService transactionService) {
        this.accountDAO = accountDAO;
        this.transactionService = transactionService;
    }

    public List<Account> getAccountsByCustomer(int customerId) throws Exception {
        return accountDAO.findByCustomerId(customerId);
    }

    public Account getAccountById(int accountId) throws Exception {
        Account acc = accountDAO.findById(accountId);
        if (acc == null) throw new AccountNotFoundException("Account ID not found: " + accountId);
        return acc;
    }

    public Account createSavingsAccount(int customerId, BigDecimal initialDeposit) throws Exception {
        if (initialDeposit == null || initialDeposit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial deposit cannot be negative.");
        }
        boolean hasSavings = getAccountsByCustomer(customerId).stream()
                .anyMatch(acc -> "SAVINGS".equals(acc.getAccountType()));
        if (hasSavings) {
            throw new IllegalStateException("A savings account already exists for this customer.");
        }
        SavingsAccount savings = new SavingsAccount(customerId, initialDeposit, 3, BigDecimal.valueOf(500));
        accountDAO.save(savings);
        return savings;
    }

    public void deleteAccount(int accountId) throws Exception {
        Account account = getAccountById(accountId);
        if ("WALLET".equals(account.getAccountType())) {
            throw new IllegalArgumentException("Wallet accounts cannot be deleted.");
        }
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Only inactive accounts with zero balance can be deleted.");
        }
        accountDAO.deleteById(accountId);
    }

    public void deposit(int accountId, BigDecimal amount, String description) throws Exception {
        Account account = getAccountById(accountId);
        account.deposit(amount);
        accountDAO.updateBalance(accountId, account.getBalance());
        String refId = transactionService.generateUniqueReference();
        Transaction tx = new Transaction(accountId, refId, "DEPOSIT", amount,
                null, null, description);
        transactionService.saveTransaction(tx);
    }

    public void withdraw(int accountId, BigDecimal amount, String description) throws Exception {
        Account account = getAccountById(accountId);
        account.withdraw(amount);
        accountDAO.updateBalance(accountId, account.getBalance());
        if (account instanceof SavingsAccount) {
            SavingsAccount savings = (SavingsAccount) account;
            accountDAO.updateWithdrawalCount(accountId, savings.getWithdrawalCount());
        }
        String refId = transactionService.generateUniqueReference();
        String txType = account instanceof SavingsAccount ? "SAVINGS_WITHDRAW" : "WITHDRAW";
        Transaction tx = new Transaction(accountId, refId, txType, amount,
                null, null, description);
        transactionService.saveTransaction(tx);
    }

    public BigDecimal getBalance(int accountId) throws Exception {
        Account acc = getAccountById(accountId);
        return acc.getBalance();
    }
}