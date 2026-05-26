package com.igirepay.LAB2_dao;

import com.igirepay.LAB1_model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

public class CustomerDAOImpl implements CustomerDAO {

    @Override
    public void save(Customer customer) throws Exception {
        String sql = "INSERT INTO customers (full_name, email, phone_number, pin_hash) VALUES (?, ? , ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, customer.getFullName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhoneNumber());
            pstmt.setString(4, customer.getPinHash());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    customer.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public Optional<Customer> findByPhoneNumber(String phoneNumber) throws Exception {
        String sql = "SELECT id, full_name, email, phone_number, pin_hash, created_at, failed_attempts FROM customers WHERE phone_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phoneNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Customer c = new Customer();
                    c.setId(rs.getInt("id"));
                    c.setFullName(rs.getString("full_name"));
                    c.setEmail(rs.getString("email"));
                    c.setPhoneNumber(rs.getString("phone_number"));
                    c.setPinHash(rs.getString("pin_hash"));
                    c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    c.setFailedAttempts(rs.getInt("failed_attempts"));
                    return Optional.of(c);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Customer> findByEmail(String email) throws Exception {
        String sql = "SELECT id, full_name, email, phone_number, pin_hash, created_at, failed_attempts FROM customers WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Customer c = new Customer();
                    c.setId(rs.getInt("id"));
                    c.setFullName(rs.getString("full_name"));
                    c.setEmail(rs.getString("email"));
                    c.setPhoneNumber(rs.getString("phone_number"));
                    c.setPinHash(rs.getString("pin_hash"));
                    c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    c.setFailedAttempts(rs.getInt("failed_attempts"));
                    return Optional.of(c);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Customer> findById(int id) throws Exception {
        String sql = "SELECT id, full_name, email, phone_number, pin_hash, created_at FROM customers WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Customer c = new Customer();
                    c.setId(rs.getInt("id"));
                    c.setFullName(rs.getString("full_name"));
                    c.setEmail(rs.getString("email"));
                    c.setPhoneNumber(rs.getString("phone_number"));
                    c.setPinHash(rs.getString("pin_hash"));
                    c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return Optional.of(c);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void updateProfile(Customer customer) throws Exception {
        String sql = "UPDATE customers SET full_name = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customer.getFullName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setInt(3, customer.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void updatePinHash(int customerId, String newPinHash) throws Exception {
        String sql = "UPDATE customers SET pin_hash = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPinHash);
            pstmt.setInt(2, customerId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void incrementFailedAttempts(String phoneNumber) throws Exception {
        String sql = "UPDATE customers SET failed_attempts = failed_attempts + 1 WHERE phone_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phoneNumber);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void resetFailedAttempts(String phoneNumber) throws Exception {
        String sql = "UPDATE customers SET failed_attempts = 0 WHERE phone_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phoneNumber);
            pstmt.executeUpdate();
        }
    }

    @Override
    public int getFailedAttempts(String phoneNumber) throws Exception {
        String sql = "SELECT failed_attempts FROM customers WHERE phone_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phoneNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("failed_attempts");
                }
            }
        }
        return 0;
    }
}