package com.igirepay.LAB1_service;

import com.igirepay.LAB2_dao.TransactionDAO;
import com.igirepay.LAB1_model.Transaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class LoanService {
    private final TransactionDAO transactionDAO;

    public LoanService(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }

    public boolean requestLoan(int customerId, BigDecimal requestedAmount) throws Exception {
        List<Transaction> allTx = transactionDAO.findAllByCustomerId(customerId);
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusDays(30);
        long recentTxCount = allTx.stream()
                .filter(tx -> tx.getCreatedAt().isAfter(oneMonthAgo))
                .count();
        boolean approved = recentTxCount >= 3 && requestedAmount.compareTo(BigDecimal.valueOf(50000)) <= 0;
        return approved;
    }
}