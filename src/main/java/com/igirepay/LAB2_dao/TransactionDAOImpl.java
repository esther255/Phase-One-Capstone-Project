package com.igirepay.LAB2_dao;

import com.igirepay.LAB1_model.Transaction;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAOImpl implements TransactionDAO {

    @Override
    public void save(Transaction t) throws Exception {
        String sql = "INSERT INTO transactions (account_id, reference_id, transaction_type, amount, sender_id, recipient_id, description) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, t.getAccountId());
            ps.setString(2, t.getReferenceId());
            ps.setString(3, t.getType());
            ps.setBigDecimal(4, t.getAmount());
            if (t.getSenderAccountId() != null) ps.setInt(5, t.getSenderAccountId()); else ps.setNull(5, Types.INTEGER);
            if (t.getRecipientAccountId() != null) ps.setInt(6, t.getRecipientAccountId()); else ps.setNull(6, Types.INTEGER);
            ps.setString(7, t.getDescription());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) t.setId(rs.getInt(1));
            }
        }
    }

    @Override
    public List<Transaction> findByAccountId(int accountId) throws Exception {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<Transaction> findAllByCustomerId(int customerId) throws Exception {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT t.* FROM transactions t JOIN accounts a ON t.account_id = a.id WHERE a.customer_id = ? ORDER BY t.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setId(rs.getInt("id"));
        t.setAccountId(rs.getInt("account_id"));
        t.setReferenceId(rs.getString("reference_id"));
        t.setType(rs.getString("transaction_type"));
        t.setAmount(rs.getBigDecimal("amount"));
        t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        t.setSenderAccountId((Integer) rs.getObject("sender_id"));
        t.setRecipientAccountId((Integer) rs.getObject("recipient_id"));
        t.setDescription(rs.getString("description"));
        return t;
    }
}