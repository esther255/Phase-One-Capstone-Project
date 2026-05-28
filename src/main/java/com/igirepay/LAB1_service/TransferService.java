package com.igirepay.LAB1_service;

import com.igirepay.LAB2_dao.*;
import com.igirepay.LAB3_exception.*;
import com.igirepay.LAB1_model.*;
import java.math.BigDecimal;

public class TransferService {
    private final AccountDAO accountDAO;
    private final CustomerDAO customerDAO;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public TransferService(AccountDAO accountDAO, CustomerDAO customerDAO,
                           AccountService accountService, TransactionService transactionService,
                           TransactionDAO transactionDAO) {
        this.accountDAO = accountDAO;
        this.customerDAO = customerDAO;
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    public String sendMoney(int senderWalletId, String recipientPhone, BigDecimal amount, String description) throws Exception {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new InvalidAmountException("Amount must be greater than 0");
        Account senderWallet = accountService.getAccountById(senderWalletId);
        if (!"WALLET".equals(senderWallet.getAccountType()))
            throw new IllegalArgumentException("Sender account is not a wallet");
        var recipientOpt = customerDAO.findByPhoneNumber(recipientPhone);
        if (recipientOpt.isEmpty()) throw new AccountNotFoundException("Recipient not found");
        int recipientCustomerId = recipientOpt.get().getId();

        if (recipientCustomerId == senderWallet.getCustomerId())
            throw new IllegalArgumentException("Cannot transfer money to yourself");
        Account recipientWallet = accountDAO.findByCustomerId(recipientCustomerId).stream()
                .filter(a -> "WALLET".equals(a.getAccountType()))
                .findFirst().orElseThrow(() -> new AccountNotFoundException("Recipient has no wallet account"));
        if (senderWallet.getBalance().compareTo(amount) < 0)
            throw new InsufficientBalanceException("Insufficient funds. Available: " + senderWallet.getBalance());

        senderWallet.withdraw(amount);
        recipientWallet.deposit(amount);
        accountDAO.updateBalance(senderWalletId, senderWallet.getBalance());
        accountDAO.updateBalance(recipientWallet.getId(), recipientWallet.getBalance());

        String sendRef = transactionService.generateUniqueReference();
        String receiveRef = transactionService.generateUniqueReference();
        Transaction senderTx = new Transaction(senderWalletId, sendRef, "SEND_MONEY", amount,
                senderWalletId, recipientWallet.getId(), description);
        transactionService.saveTransaction(senderTx);
        Transaction recipientTx = new Transaction(recipientWallet.getId(), receiveRef, "RECEIVE_MONEY", amount,
                senderWalletId, recipientWallet.getId(), description);
        transactionService.saveTransaction(recipientTx);
        return sendRef;
    }
}