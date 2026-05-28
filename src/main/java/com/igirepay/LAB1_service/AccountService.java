package com.igirepay.LAB1_service;

import com.igirepay.LAB2_dao.AccountDAO;
import com.igirepay.LAB3_exception.*;
import com.igirepay.LAB1_model.*;
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

    public Account getAccountById(int id) throws Exception {
        Account acc = accountDAO.findById(id);
        if (acc == null) throw new AccountNotFoundException("Account not found");
        return acc;
    }

    public void deposit(int accountId, BigDecimal amount, String desc) throws Exception {
        Account acc = getAccountById(accountId);
        acc.deposit(amount);
        accountDAO.updateBalance(accountId, acc.getBalance());
        String ref = transactionService.generateUniqueReference();
        Transaction tx = new Transaction(accountId, ref, "DEPOSIT", amount, null, null, desc);
        transactionService.saveTransaction(tx);
    }

    public void withdraw(int accountId, BigDecimal amount, String desc) throws Exception {
        Account acc = getAccountById(accountId);
        acc.withdraw(amount);
        accountDAO.updateBalance(accountId, acc.getBalance());
        if (acc instanceof SavingsAccount) {
            accountDAO.updateWithdrawalCount(accountId, ((SavingsAccount) acc).getWithdrawalCount());
        }
        String ref = transactionService.generateUniqueReference();
        String type = acc instanceof SavingsAccount ? "SAVINGS_WITHDRAW" : "WITHDRAW";
        Transaction tx = new Transaction(accountId, ref, type, amount, null, null, desc);
        transactionService.saveTransaction(tx);
    }

    public BigDecimal getBalance(int accountId) throws Exception {
        return getAccountById(accountId).getBalance();
    }

    public void createWalletAccount(int customerId, BigDecimal initial) throws Exception {
        boolean alreadyHasWallet = accountDAO.findByCustomerId(customerId).stream()
                .anyMatch(a -> "WALLET".equals(a.getAccountType()));
        if (alreadyHasWallet) throw new Exception("You already have a wallet account");
        WalletAccount acc = new WalletAccount(customerId, initial);
        accountDAO.save(acc);
    }

    public void createSavingsAccount(int customerId, BigDecimal initial, int limit, BigDecimal fee) throws Exception {

        boolean alreadyHasSavings = accountDAO.findByCustomerId(customerId).stream()
                .anyMatch(a -> "SAVINGS".equals(a.getAccountType()));
        if (alreadyHasSavings) throw new Exception("You already have a savings account");
        SavingsAccount acc = new SavingsAccount(customerId, initial, limit, fee);
        accountDAO.save(acc);
    }

    public void deleteAccount(int accountId) throws Exception {
        Account acc = getAccountById(accountId);
        if (acc.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new Exception("Cannot delete account with non-zero balance");
        }
        accountDAO.deleteById(accountId);
    }
}