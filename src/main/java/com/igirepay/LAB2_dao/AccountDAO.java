package com.igirepay.LAB2_dao;

import com.igirepay.LAB1_model.Account;
import java.math.BigDecimal;
import java.util.List;

public interface AccountDAO {
    void save(Account account) throws Exception;
    Account findById(int id) throws Exception;
    List<Account> findByCustomerId(int customerId) throws Exception;
    void updateBalance(int accountId, BigDecimal newBalance) throws Exception;
    void updateWithdrawalCount(int savingsAccountId, int newCount) throws Exception;
    void deleteById(int accountId) throws Exception;
}