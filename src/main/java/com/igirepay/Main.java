package com.igirepay;

import com.igirepay.LAB1_service.*;
import com.igirepay.LAB2_dao.*;
import com.igirepay.LAB2_dao.DatabaseInitializer;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage primaryStage;
    private ScreenManager screenManager;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;

        // Initialize DAOs
        CustomerDAO customerDAO = new CustomerDAOImpl();
        AccountDAO accountDAO = new AccountDAOImpl();
        TransactionDAO transactionDAO = new TransactionDAOImpl();
        ProcessedRequestDAO processedRequestDAO = new ProcessedRequestDAOImpl();

        // Initialize Services
        AuthService authService = new AuthService(customerDAO, accountDAO);
        TransactionService transactionService = new TransactionService(transactionDAO, processedRequestDAO);
        AccountService accountService = new AccountService(accountDAO, transactionService);

        // Initialize or migrate database schema before using DAOs
        DatabaseInitializer.initializeDatabase();

        // FIXED: pass transactionDAO as 5th argument
        TransferService transferService = new TransferService(accountDAO, customerDAO,
                accountService, transactionService, transactionDAO);

        LoanService loanService = new LoanService(transactionDAO);
        CSVExportService csvExportService = new CSVExportService();

        // Optional: Insert sample data (uncomment if needed)
        // DataInitializer.initSampleData(customerDAO, accountDAO);

        screenManager = new ScreenManager(primaryStage, authService, accountService,
                transactionService, transferService, loanService, csvExportService);
        screenManager.showLoginScreen();
        primaryStage.setTitle("IgirePay - MTN MoMo Desktop");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}