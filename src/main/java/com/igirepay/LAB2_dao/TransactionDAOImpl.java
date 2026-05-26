package com.igirepay.LAB2_dao;

import com.igirepay.LAB1_model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAOImpl implements TransactionDAO {
    @Override
    public void save(Transaction transaction) throws Exception {
        String sql = "INSERT INTO transactions (account_id, reference_id, transaction_type, amount, sender_id, recipient_id, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, transaction.getAccountId());
            pstmt.setString(2, transaction.getReferenceId());
            pstmt.setString(3, transaction.getType());
            pstmt.setBigDecimal(4, transaction.getAmount());
            if (transaction.getSenderAccountId() != null) pstmt.setInt(5, transaction.getSenderAccountId());
            else pstmt.setNull(5, Types.INTEGER);
            if (transaction.getRecipientAccountId() != null) pstmt.setInt(6, transaction.getRecipientAccountId());
            else pstmt.setNull(6, Types.INTEGER);
            pstmt.setString(7, transaction.getDescription());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    transaction.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Transaction> findByAccountId(int accountId) throws Exception {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT id, account_id, reference_id, transaction_type, amount, created_at, sender_id, recipient_id, description " +
                "FROM transactions WHERE account_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Transaction t = new Transaction();
                    t.setId(rs.getInt("id"));
                    t.setAccountId(rs.getInt("account_id"));
                    t.setReferenceId(rs.getString("reference_id"));
                    t.setType(rs.getString("transaction_type"));
                    t.setAmount(rs.getBigDecimal("amount"));
                    t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    t.setSenderAccountId(rs.getInt("sender_id"));
                    if (rs.wasNull()) t.setSenderAccountId(null);
                    t.setRecipientAccountId(rs.getInt("recipient_id"));
                    if (rs.wasNull()) t.setRecipientAccountId(null);
                    t.setDescription(rs.getString("description"));
                    list.add(t);
                }
            }
        }
        return list;
    }

    @Override
    public List<Transaction> findAllByCustomerId(int customerId) throws Exception {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT t.id, t.account_id, t.reference_id, t.transaction_type, t.amount, t.created_at, t.sender_id, t.recipient_id, t.description " +
                "FROM transactions t JOIN accounts a ON t.account_id = a.id WHERE a.customer_id = ? ORDER BY t.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Transaction t = new Transaction();
                    t.setId(rs.getInt("id"));
                    t.setAccountId(rs.getInt("account_id"));
                    t.setReferenceId(rs.getString("reference_id"));
                    t.setType(rs.getString("transaction_type"));
                    t.setAmount(rs.getBigDecimal("amount"));
                    t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    t.setSenderAccountId(rs.getInt("sender_id"));
                    if (rs.wasNull()) t.setSenderAccountId(null);
                    t.setRecipientAccountId(rs.getInt("recipient_id"));
                    if (rs.wasNull()) t.setRecipientAccountId(null);
                    t.setDescription(rs.getString("description"));
                    list.add(t);
                }
            }
        }
        return list;
    }
}