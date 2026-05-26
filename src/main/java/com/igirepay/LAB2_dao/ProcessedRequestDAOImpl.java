package com.igirepay.LAB2_dao;

import java.sql.*;

public class ProcessedRequestDAOImpl implements ProcessedRequestDAO {
    @Override
    public boolean exists(String refId) throws Exception {
        String sql = "SELECT 1 FROM processed_requests WHERE reference_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, refId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public void save(String refId) throws Exception {
        String sql = "INSERT INTO processed_requests (reference_id) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, refId);
            ps.executeUpdate();
        }
    }
}