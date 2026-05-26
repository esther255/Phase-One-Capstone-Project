package com.igirepay.LAB2_dao;

import com.igirepay.LAB1_model.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                account.setId(rs.getInt(1));
            }
        }
    }

    @Override
    public Account findById(int id) throws Exception {
        String sql = "SELECT id, customer_id, account_type, balance, created_at FROM accounts WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
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

                // Load savings-specific fields if needed (withdrawal limit/count)
                if (acc instanceof SavingsAccount) {
                    loadSavingsDetails((SavingsAccount) acc, conn);
                }
                return acc;
            }
        }
        return null;
    }

    private void loadSavingsDetails(SavingsAccount savings, Connection conn) throws SQLException {
        // If you add withdrawal_limit and withdrawal_fee columns to accounts table, query them.
        // For now, we'll use defaults; later we can add columns.
        // Simpler: store in a separate savings_config table or add columns.
        // We'll assume accounts table has withdrawal_limit (int), withdrawal_count (int), withdrawal_fee (decimal)
        String sql = "SELECT withdrawal_limit, withdrawal_count, withdrawal_fee FROM accounts WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, savings.getId());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                savings.setWithdrawalLimit(rs.getInt("withdrawal_limit"));
                savings.setWithdrawalCount(rs.getInt("withdrawal_count"));
                savings.setWithdrawalFee(rs.getBigDecimal("withdrawal_fee"));
            }
        }
    }

    @Override
    public List<Account> findByCustomerId(int customerId) throws Exception {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT id, customer_id, account_type, balance, created_at FROM accounts WHERE customer_id::text = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, String.valueOf(customerId));
            ResultSet rs = pstmt.executeQuery();
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
                    loadSavingsDetails((SavingsAccount) acc, conn);
                }
                accounts.add(acc);
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
