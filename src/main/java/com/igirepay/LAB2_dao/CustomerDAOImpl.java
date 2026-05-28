package com.igirepay.LAB2_dao;

import com.igirepay.LAB1_model.Customer;
import com.igirepay.LAB3_util.PasswordUtil;
import java.sql.*;
import java.util.Optional;

public class CustomerDAOImpl implements CustomerDAO {

    @Override
    public void save(Customer customer) throws Exception {
        String sql = "INSERT INTO customers (full_name, email, phone_number, pin) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, customer.getFullName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPhoneNumber());
            ps.setString(4, customer.getPinHash());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) customer.setId(rs.getInt(1));
        }
    }

    @Override
    public Optional<Customer> findByPhoneNumber(String phone) throws Exception {
        String sql = "SELECT id, full_name, email, phone_number, pin, failed_attempts, created_at FROM customers WHERE phone_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Customer> findByEmail(String email) throws Exception {
        return Optional.empty();
    }

    @Override
    public Optional<Customer> findById(int id) throws Exception {
        String sql = "SELECT id, full_name, email, phone_number, pin, failed_attempts, created_at FROM customers WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    @Override
    public void updateProfile(Customer customer) throws Exception {
        String sql = "UPDATE customers SET full_name = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getFullName());
            ps.setString(2, customer.getEmail());
            ps.setInt(3, customer.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void updatePin(int customerId, String pin) throws Exception {
        String sql = "UPDATE customers SET pin = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, PasswordUtil.hashPin(pin));
            ps.setInt(2, customerId);
            ps.executeUpdate();
        }
    }

    @Override
    public void incrementFailedAttempts(String phone) throws Exception {
        String sql = "UPDATE customers SET failed_attempts = failed_attempts + 1 WHERE phone_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.executeUpdate();
        }
    }

    @Override
    public void resetFailedAttempts(String phone) throws Exception {
        String sql = "UPDATE customers SET failed_attempts = 0 WHERE phone_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.executeUpdate();
        }
    }

    @Override
    public int getFailedAttempts(String phone) throws Exception {
        String sql = "SELECT failed_attempts FROM customers WHERE phone_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("failed_attempts");
        }
        return 0;
    }

    @Override
    public boolean existsByPhoneNumber(String phone) throws Exception {
        String sql = "SELECT 1 FROM customers WHERE phone_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setId(rs.getInt("id"));
        c.setFullName(rs.getString("full_name"));
        c.setEmail(rs.getString("email"));
        c.setPhoneNumber(rs.getString("phone_number"));
        c.setPin(rs.getString("pin"));
        c.setFailedAttempts(rs.getInt("failed_attempts"));
        c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return c;
    }
}