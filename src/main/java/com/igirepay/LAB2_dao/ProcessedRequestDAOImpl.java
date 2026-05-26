package com.igirepay.LAB2_dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProcessedRequestDAOImpl implements ProcessedRequestDAO {
    @Override
    public boolean exists(String referenceId) throws Exception {
        String sql = "SELECT 1 FROM processed_requests WHERE reference_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, referenceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public void save(String referenceId) throws Exception {
        String sql = "INSERT INTO processed_requests (reference_id) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, referenceId);
            pstmt.executeUpdate();
        }
    }
}