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
    private final TransactionDAO transactionDAO;

    public TransferService(AccountDAO accountDAO, CustomerDAO customerDAO,
                           AccountService accountService, TransactionService transactionService,
                           TransactionDAO transactionDAO) {
        this.accountDAO = accountDAO;
        this.customerDAO = customerDAO;
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.transactionDAO = transactionDAO;
    }

    public String sendMoney(int senderWalletId, String recipientPhone, BigDecimal amount, String description) throws Exception {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new InvalidAmountException("Amount > 0");
        var recipientOpt = customerDAO.findByPhoneNumber(recipientPhone);
        if (recipientOpt.isEmpty()) throw new AccountNotFoundException("Recipient not found");
        int recipientId = recipientOpt.get().getId();
        Account recipientWallet = accountDAO.findByCustomerId(recipientId).stream()
                .filter(a -> "WALLET".equals(a.getAccountType()))
                .findFirst().orElseThrow(() -> new AccountNotFoundException("Recipient has no wallet"));
        Account senderWallet = accountService.getAccountById(senderWalletId);
        if (!"WALLET".equals(senderWallet.getAccountType()))
            throw new IllegalArgumentException("Sender not a wallet");
        if (senderWallet.getBalance().compareTo(amount) < 0)
            throw new InsufficientBalanceException("Insufficient funds");
        senderWallet.withdraw(amount);
        recipientWallet.deposit(amount);
        accountDAO.updateBalance(senderWalletId, senderWallet.getBalance());
        accountDAO.updateBalance(recipientWallet.getId(), recipientWallet.getBalance());
        String ref = transactionService.generateUniqueReference();
        Transaction senderTx = new Transaction(senderWalletId, ref, "SEND_MONEY", amount,
                senderWalletId, recipientWallet.getId(), description);
        transactionService.saveTransaction(senderTx);
        Transaction recipientTx = new Transaction(recipientWallet.getId(), ref, "RECEIVE_MONEY", amount,
                senderWalletId, recipientWallet.getId(), description);
        transactionDAO.save(recipientTx);
        return ref;
    }
}