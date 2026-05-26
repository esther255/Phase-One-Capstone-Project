package com.igirepay.LAB2_dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class                                                                                                                                                                DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/igirepay";
    private static final String USER = "postgres";      // change to your DB user
    private static final String PASSWORD = "123";  // change to your DB password

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
