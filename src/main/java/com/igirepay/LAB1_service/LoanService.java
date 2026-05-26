package com.igirepay.LAB1_service;

import com.igirepay.LAB2_dao.TransactionDAO;
import com.igirepay.LAB1_model.Transaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LoanService {
    private final TransactionDAO transactionDAO;
    public LoanService(TransactionDAO transactionDAO) { this.transactionDAO = transactionDAO; }

    public boolean requestLoan(int customerId, BigDecimal amount) throws Exception {
        long count = transactionDAO.findAllByCustomerId(customerId).stream()
                .filter(t -> t.getCreatedAt().isAfter(LocalDateTime.now().minusDays(30)))
                .count();
        return count >= 3 && amount.compareTo(BigDecimal.valueOf(50000)) <= 0;
    }
}