package com.igirepay.LAB1_service;

import com.igirepay.LAB2_dao.ProcessedRequestDAO;
import com.igirepay.LAB2_dao.TransactionDAO;
import com.igirepay.LAB3_exception.DuplicateTransactionException;
import com.igirepay.LAB1_model.Transaction;
import com.igirepay.LAB3_util.ReferenceIdGenerator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TransactionService {
    private final TransactionDAO transactionDAO;
    private final ProcessedRequestDAO processedRequestDAO;
    private static final Set<String> processedRefIdsInMemory = new HashSet<>();

    public TransactionService(TransactionDAO transactionDAO, ProcessedRequestDAO processedRequestDAO) {
        this.transactionDAO = transactionDAO;
        this.processedRequestDAO = processedRequestDAO;
    }

    public String generateUniqueReference() throws Exception {
        String ref;
        do {
            ref = ReferenceIdGenerator.generate();
        } while (isDuplicate(ref));
        return ref;
    }

    private boolean isDuplicate(String refId) throws Exception {
        if (processedRefIdsInMemory.contains(refId)) return true;
        return processedRequestDAO.exists(refId);
    }

    public void saveTransaction(Transaction transaction) throws Exception {
        String refId = transaction.getReferenceId();
        if (isDuplicate(refId)) {
            throw new DuplicateTransactionException("Duplicate transaction reference: " + refId);
        }
        processedRequestDAO.save(refId);
        processedRefIdsInMemory.add(refId);
        transactionDAO.save(transaction);
    }

    public List<Transaction> getTransactionHistory(int customerId) throws Exception {
        return transactionDAO.findAllByCustomerId(customerId);
    }

    public List<Transaction> getAccountTransactions(int accountId) throws Exception {
        return transactionDAO.findByAccountId(accountId);
    }
}