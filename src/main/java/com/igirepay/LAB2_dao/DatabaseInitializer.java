package com.igirepay.LAB2_dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            if (!tableExists(conn, "customers")) {
                createCustomersTable(conn);
            } else {
                addColumnIfMissing(conn, "customers", "full_name", "TEXT NOT NULL DEFAULT ''");
                addColumnIfMissing(conn, "customers", "email", "TEXT NOT NULL DEFAULT ''");
                addColumnIfMissing(conn, "customers", "phone_number", "TEXT NOT NULL DEFAULT ''");
                addColumnIfMissing(conn, "customers", "pin_hash", "TEXT NOT NULL DEFAULT ''");
                addColumnIfMissing(conn, "customers", "created_at", "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");
                addColumnIfMissing(conn, "customers", "failed_attempts", "INTEGER NOT NULL DEFAULT 0");
                ensureSerialPrimaryKey(conn, "customers", "id");
            }

            if (!tableExists(conn, "accounts")) {
                createAccountsTable(conn);
            } else {
                addColumnIfMissing(conn, "accounts", "created_at", "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");
                addColumnIfMissing(conn, "accounts", "withdrawal_limit", "INTEGER NOT NULL DEFAULT 0");
                addColumnIfMissing(conn, "accounts", "withdrawal_count", "INTEGER NOT NULL DEFAULT 0");
                addColumnIfMissing(conn, "accounts", "withdrawal_fee", "NUMERIC NOT NULL DEFAULT 0");
                ensureSerialPrimaryKey(conn, "accounts", "id");
            }

            if (!tableExists(conn, "transactions")) {
                createTransactionsTable(conn);
            } else {
                addColumnIfMissing(conn, "transactions", "created_at", "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");
                ensureSerialPrimaryKey(conn, "transactions", "id");
            }

            if (!tableExists(conn, "processed_requests")) {
                createProcessedRequestsTable(conn);
            }

            conn.commit();
        }
    }

    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        String sql = "SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tableName);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static boolean columnExists(Connection conn, String tableName, String columnName) throws SQLException {
        String sql = "SELECT 1 FROM information_schema.columns WHERE table_schema = 'public' AND table_name = ? AND column_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tableName);
            pstmt.setString(2, columnName);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static void addColumnIfMissing(Connection conn, String tableName, String columnName, String definition) throws SQLException {
        if (!columnExists(conn, tableName, columnName)) {
            try (Statement stmt = conn.createStatement()) {
                String sql = String.format("ALTER TABLE %s ADD COLUMN %s %s", tableName, columnName, definition);
                stmt.executeUpdate(sql);
            }
        }
    }

    private static void ensureSerialPrimaryKey(Connection conn, String tableName, String columnName) throws SQLException {
        String defaultValue = getColumnDefault(conn, tableName, columnName);
        if (defaultValue == null || !defaultValue.contains("nextval")) {
            String sequenceName = tableName + "_" + columnName + "_seq";
            if (!sequenceExists(conn, sequenceName)) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(String.format("CREATE SEQUENCE %s START 1", sequenceName));
                }
            }
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(String.format("ALTER TABLE %s ALTER COLUMN %s SET DEFAULT nextval('%s'::regclass)", tableName, columnName, sequenceName));
                stmt.execute(String.format("SELECT setval('%s', COALESCE(MAX(%s)::bigint, 0) + 1, false) FROM %s", sequenceName, columnName, tableName));
            }
        }
    }

    private static String getColumnDefault(Connection conn, String tableName, String columnName) throws SQLException {
        String sql = "SELECT column_default FROM information_schema.columns WHERE table_schema = 'public' AND table_name = ? AND column_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tableName);
            pstmt.setString(2, columnName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("column_default");
                }
            }
        }
        return null;
    }

    private static boolean sequenceExists(Connection conn, String sequenceName) throws SQLException {
        String sql = "SELECT 1 FROM information_schema.sequences WHERE sequence_schema = 'public' AND sequence_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sequenceName);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static void createCustomersTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE customers ("
                + "id SERIAL PRIMARY KEY, "
                + "full_name TEXT NOT NULL, "
                + "email TEXT NOT NULL, "
                + "phone_number TEXT UNIQUE NOT NULL, "
                + "pin_hash TEXT NOT NULL, "
                + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                + "failed_attempts INTEGER NOT NULL DEFAULT 0"
                + ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    private static void createAccountsTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE accounts ("
                + "id SERIAL PRIMARY KEY, "
                + "customer_id INTEGER NOT NULL REFERENCES customers(id), "
                + "account_type TEXT NOT NULL, "
                + "balance NUMERIC NOT NULL DEFAULT 0, "
                + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                + "withdrawal_limit INTEGER NOT NULL DEFAULT 0, "
                + "withdrawal_count INTEGER NOT NULL DEFAULT 0, "
                + "withdrawal_fee NUMERIC NOT NULL DEFAULT 0"
                + ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    private static void createTransactionsTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE transactions ("
                + "id SERIAL PRIMARY KEY, "
                + "account_id INTEGER NOT NULL REFERENCES accounts(id), "
                + "reference_id TEXT NOT NULL, "
                + "transaction_type TEXT NOT NULL, "
                + "amount NUMERIC NOT NULL, "
                + "sender_id INTEGER, "
                + "recipient_id INTEGER, "
                + "description TEXT, "
                + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
                + ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    private static void createProcessedRequestsTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE processed_requests (reference_id TEXT PRIMARY KEY)";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }
}
