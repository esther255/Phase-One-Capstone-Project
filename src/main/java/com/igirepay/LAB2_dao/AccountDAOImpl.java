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
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, account.getCustomerId());
            ps.setString(2, account.getAccountType());
            ps.setBigDecimal(3, account.getBalance());
            if (account instanceof SavingsAccount) {
                SavingsAccount savings = (SavingsAccount) account;
                ps.setInt(4, savings.getWithdrawalLimit());
                ps.setInt(5, savings.getWithdrawalCount());
                ps.setBigDecimal(6, savings.getWithdrawalFee());
            } else {
                ps.setInt(4, 0);
                ps.setInt(5, 0);
                ps.setBigDecimal(6, BigDecimal.ZERO);
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) account.setId(rs.getInt(1));
            }
        }
    }

    @Override
    public Account findById(int id) throws Exception {
        String sql = "SELECT id, customer_id, account_type, balance, created_at, withdrawal_limit, withdrawal_count, withdrawal_fee FROM accounts WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public List<Account> findByCustomerId(int customerId) throws Exception {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT id, customer_id, account_type, balance, created_at, withdrawal_limit, withdrawal_count, withdrawal_fee FROM accounts WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public void updateBalance(int accountId, BigDecimal newBalance) throws Exception {
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, newBalance);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        }
    }

    @Override
    public void updateWithdrawalCount(int savingsAccountId, int newCount) throws Exception {
        String sql = "UPDATE accounts SET withdrawal_count = ? WHERE id = ? AND account_type = 'SAVINGS'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newCount);
            ps.setInt(2, savingsAccountId);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteById(int accountId) throws Exception {
        String sql = "DELETE FROM accounts WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new Exception("Account not found");
        }
    }

    private Account mapRow(ResultSet rs) throws SQLException {
        String type = rs.getString("account_type");
        Account acc;
        if ("WALLET".equals(type)) acc = new WalletAccount();
        else acc = new SavingsAccount();
        acc.setId(rs.getInt("id"));
        acc.setCustomerId(rs.getInt("customer_id"));
        acc.setAccountType(type);
        acc.setBalance(rs.getBigDecimal("balance"));
        acc.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        if (acc instanceof SavingsAccount) {
            SavingsAccount s = (SavingsAccount) acc;
            s.setWithdrawalLimit(rs.getInt("withdrawal_limit"));
            s.setWithdrawalCount(rs.getInt("withdrawal_count"));
            s.setWithdrawalFee(rs.getBigDecimal("withdrawal_fee"));
        }
        return acc;
    }
}