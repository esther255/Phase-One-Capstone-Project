package com.igirepay.LAB2_dao;

import com.igirepay.LAB1_model.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AccountDAOImpl implements AccountDAO {

    @Override
    public void save(Account account) throws Exception {
        String sql = "INSERT INTO accounts (customer_id, account_type, balance, withdrawal_limit, withdrawal_count, withdrawal_fee) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, account.getCustomerId());
            pstmt.setString(2, account.getAccountType());
            pstmt.setBigDecimal(3, account.getBalance());
            if (account instanceof SavingsAccount) {
                SavingsAccount savings = (SavingsAccount) account;
                pstmt.setInt(4, savings.getWithdrawalLimit());
                pstmt.setInt(5, savings.getWithdrawalCount());
                pstmt.setBigDecimal(6, savings.getWithdrawalFee());
            } else {
                pstmt.setInt(4, 0);
                pstmt.setInt(5, 0);
                pstmt.setBigDecimal(6, BigDecimal.ZERO);
            }
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    account.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public Account findById(int id) throws Exception {
        String sql = "SELECT id, customer_id, account_type, balance, created_at, withdrawal_limit, withdrawal_count, withdrawal_fee FROM accounts WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String type = rs.getString("account_type");
                    Account acc;
                    if ("WALLET".equals(type)) {
                        acc = new WalletAccount();
                    } else {
                        acc = new SavingsAccount();
                    }
                    acc.setId(rs.getInt("id"));
                    acc.setCustomerId(rs.getInt("customer_id"));
                    acc.setAccountType(type);
                    acc.setBalance(rs.getBigDecimal("balance"));
                    acc.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                    if (acc instanceof SavingsAccount) {
                        SavingsAccount savings = (SavingsAccount) acc;
                        savings.setWithdrawalLimit(rs.getInt("withdrawal_limit"));
                        savings.setWithdrawalCount(rs.getInt("withdrawal_count"));
                        savings.setWithdrawalFee(rs.getBigDecimal("withdrawal_fee"));
                    }
                    return acc;
                }
            }
        }
        return null;
    }

    @Override
    public List<Account> findByCustomerId(int customerId) throws Exception {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT id, customer_id, account_type, balance, created_at, withdrawal_limit, withdrawal_count, withdrawal_fee FROM accounts WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String type = rs.getString("account_type");
                    Account acc;
                    if ("WALLET".equals(type)) {
                        acc = new WalletAccount();
                    } else {
                        acc = new SavingsAccount();
                    }
                    acc.setId(rs.getInt("id"));
                    acc.setCustomerId(rs.getInt("customer_id"));
                    acc.setAccountType(type);
                    acc.setBalance(rs.getBigDecimal("balance"));
                    acc.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    if (acc instanceof SavingsAccount) {
                        SavingsAccount savings = (SavingsAccount) acc;
                        savings.setWithdrawalLimit(rs.getInt("withdrawal_limit"));
                        savings.setWithdrawalCount(rs.getInt("withdrawal_count"));
                        savings.setWithdrawalFee(rs.getBigDecimal("withdrawal_fee"));
                    }
                    accounts.add(acc);
                }
            }
        }
        return accounts;
    }

    @Override
    public void updateBalance(int accountId, BigDecimal newBalance) throws Exception {
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, newBalance);
            pstmt.setInt(2, accountId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void updateWithdrawalCount(int savingsAccountId, int newCount) throws Exception {
        String sql = "UPDATE accounts SET withdrawal_count = ? WHERE id = ? AND account_type = 'SAVINGS'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newCount);
            pstmt.setInt(2, savingsAccountId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteById(int accountId) throws Exception {
        String sql = "DELETE FROM accounts WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            pstmt.executeUpdate();
        }
    }
}
