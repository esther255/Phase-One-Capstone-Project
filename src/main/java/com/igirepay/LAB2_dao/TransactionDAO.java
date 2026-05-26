package com.igirepay.LAB2_dao;

import com.igirepay.LAB1_model.Transaction;  // ADD THIS IMPORT
import java.util.List;
import java.util.Scanner;

public interface TransactionDAO {
    void save(Transaction transaction) throws Exception;
    List<Transaction> findByAccountId(int accountId) throws Exception;
    List<Transaction> findAllByCustomerId(int customerId) throws Exception;
}