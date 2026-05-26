package com.igirepay.LAB1_service;

import com.igirepay.LAB2_dao.AccountDAO;
import com.igirepay.LAB2_dao.CustomerDAO;
import com.igirepay.LAB2_dao.TransactionDAO;
import com.igirepay.LAB3_exception.*;
import com.igirepay.LAB1_model.Account;
import com.igirepay.LAB1_model.Transaction;
import java.math.BigDecimal;
import java.util.Scanner;

public class TransferService {
    private final AccountDAO accountDAO;
    private final CustomerDAO customerDAO;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final TransactionDAO transactionDAO;   // ADDED

    public TransferService(AccountDAO accountDAO, CustomerDAO customerDAO,
                           AccountService accountService, TransactionService transactionService,
                           TransactionDAO transactionDAO) {   // ADDED parameter
        this.accountDAO = accountDAO;
        this.customerDAO = customerDAO;
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.transactionDAO = transactionDAO;
    }

    public String sendMoney(int senderWalletId, String recipientPhone, BigDecimal amount, String description) throws Exception {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be positive");
        }
        var recipientOpt = customerDAO.findByPhoneNumber(recipientPhone);
        if (!recipientOpt.isPresent()) {
            throw new AccountNotFoundException("Recipient not found with phone: " + recipientPhone);
        }
        int recipientCustomerId = recipientOpt.get().getId();
        Account recipientWallet = accountDAO.findByCustomerId(recipientCustomerId).stream()
                .filter(acc -> "WALLET".equals(acc.getAccountType()))
                .findFirst()
                .orElseThrow(() -> new AccountNotFoundException("Recipient has no wallet account"));
        Account senderWallet = accountService.getAccountById(senderWalletId);
        if (!"WALLET".equals(senderWallet.getAccountType())) {
            throw new IllegalArgumentException("Sender account is not a wallet account");
        }
        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance. Available: " + senderWallet.getBalance());
        }
        senderWallet.withdraw(amount);
        recipientWallet.deposit(amount);
        accountDAO.updateBalance(senderWalletId, senderWallet.getBalance());
        accountDAO.updateBalance(recipientWallet.getId(), recipientWallet.getBalance());

        String refId = transactionService.generateUniqueReference();

        // Sender transaction
        Transaction senderTx = new Transaction(senderWalletId, refId, "SEND_MONEY", amount,
                senderWalletId, recipientWallet.getId(), description);
        transactionService.saveTransaction(senderTx);   // this marks refId as processed

        // Recipient transaction – reuse same refId (already processed)
        Transaction recipientTx = new Transaction(recipientWallet.getId(), refId, "RECEIVE_MONEY", amount,
                senderWalletId, recipientWallet.getId(), description);
        transactionDAO.save(recipientTx);   // direct insert, no duplicate check

        return refId;
    }
}