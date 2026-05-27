package com.igirepay;

import com.igirepay.LAB1_service.*;
import com.igirepay.LAB2_dao.*;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Initialize database tables (optional – if they don't exist)
        DatabaseInitializer.initializeDatabase();

        try {
            // Initialize DAOs
            CustomerDAO customerDAO = new CustomerDAOImpl();
            AccountDAO accountDAO = new AccountDAOImpl();
            TransactionDAO transactionDAO = new TransactionDAOImpl();
            ProcessedRequestDAO processedRequestDAO = new ProcessedRequestDAOImpl();

            // Initialize Services
            AuthService authService = new AuthService(customerDAO);
            TransactionService transactionService = new TransactionService(transactionDAO, processedRequestDAO);
            AccountService accountService = new AccountService(accountDAO, transactionService);
            TransferService transferService = new TransferService(accountDAO, customerDAO, accountService, transactionService, transactionDAO);
            LoanService loanService = new LoanService(transactionDAO);
            CSVExportService csvExportService = new CSVExportService();

            // Create ScreenManager with all services
            ScreenManager screenManager = new ScreenManager(primaryStage, authService, accountService,
                    transactionService, transferService, loanService, csvExportService);

            // Show login screen first
            screenManager.showLoginScreen();
            primaryStage.setTitle("IgirePay - Desktop");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}