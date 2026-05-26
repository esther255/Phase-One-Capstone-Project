package com.igirepay.LAB3_ui;

import com.igirepay.ScreenManager;
import com.igirepay.LAB1_model.Account;
import com.igirepay.LAB1_service.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.math.BigDecimal;
import java.util.List;

public class DashboardController {
    private final ScreenManager screenManager;
    private final int customerId;
    private final String fullName;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final TransferService transferService;
    private final LoanService loanService;
    private final CSVExportService csvExportService;
    private VBox view;
    private Text balanceText;
    private Label hiddenBalanceLabel;
    private Label walletSummaryLabel;
    private Label savingsSummaryLabel;
    private boolean balanceVisible = false;
    private int walletAccountId;
    private int savingsAccountId;
    private BigDecimal walletBalance;

    public DashboardController(ScreenManager screenManager, int customerId, String fullName,
                               AccountService accountService, TransactionService transactionService,
                               TransferService transferService, LoanService loanService,
                               CSVExportService csvExportService) {
        this.screenManager = screenManager;
        this.customerId = customerId;
        this.fullName = fullName;
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.transferService = transferService;
        this.loanService = loanService;
        this.csvExportService = csvExportService;
        loadAccounts();
        buildUI();
    }

    private void loadAccounts() {
        walletBalance = BigDecimal.ZERO;
        try {
            List<Account> accounts = accountService.getAccountsByCustomer(customerId);
            for (Account acc : accounts) {
                if ("WALLET".equals(acc.getAccountType())) {
                    walletAccountId = acc.getId();
                    walletBalance = acc.getBalance() != null ? acc.getBalance() : BigDecimal.ZERO;
                } else if ("SAVINGS".equals(acc.getAccountType())) {
                    savingsAccountId = acc.getId();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildUI() {
        view = new VBox(15);
        view.setPadding(new Insets(20));
        view.setStyle("-fx-background-color: linear-gradient(to bottom, #1a237e, #0d47a1);");

        Label welcome = new Label("Welcome, " + fullName);
        welcome.setStyle("-fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold;");

        walletSummaryLabel = new Label("Wallet Account: " + (walletBalance != null ? walletBalance.toString() : "0"));
        walletSummaryLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14;");
        savingsSummaryLabel = new Label(savingsAccountId != 0 ? "Savings Account: Active" : "Savings Account: Not created");
        savingsSummaryLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14;");

        VBox balanceCard = new VBox(5);
        balanceCard.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 15;");
        Label balanceLabel = new Label("Available Balance");
        balanceLabel.setStyle("-fx-font-weight: bold;");
        hiddenBalanceLabel = new Label("******");
        hiddenBalanceLabel.setStyle("-fx-font-size: 28; -fx-font-family: monospace;");
        balanceText = new Text(walletBalance != null ? walletBalance.toString() : "0");
        balanceText.setStyle("-fx-font-size: 28; -fx-font-family: monospace;");
        balanceText.setVisible(false);
        Button toggleBtn = new Button("👁️ Show");
        toggleBtn.setOnAction(e -> toggleBalance());
        balanceCard.getChildren().addAll(balanceLabel, hiddenBalanceLabel, balanceText, toggleBtn);

        GridPane actions = new GridPane();
        actions.setHgap(15);
        actions.setVgap(15);
        actions.setAlignment(Pos.CENTER);
        String[] btnNames = {"Send Money", "Deposit", "Withdraw", "Savings", "Accounts", "History", "Profile", "Change PIN", "Loan"};
        int row = 0, col = 0;
        for (String name : btnNames) {
            Button btn = new Button(name);
            btn.setPrefSize(150, 80);
            btn.setStyle("-fx-font-size: 14; -fx-background-color: #ffb300; -fx-text-fill: #1a237e;");
            btn.setOnAction(e -> handleAction(name));
            actions.add(btn, col, row);
            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }

        Button logout = new Button("Logout");
        logout.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white;");
        logout.setOnAction(e -> screenManager.showLoginScreen());

        view.getChildren().addAll(welcome, walletSummaryLabel, savingsSummaryLabel, balanceCard, actions, logout);
    }

    private void toggleBalance() {
        balanceVisible = !balanceVisible;
        hiddenBalanceLabel.setVisible(!balanceVisible);
        balanceText.setVisible(balanceVisible);
    }

    private void handleAction(String action) {
        switch (action) {
            case "Send Money":
                screenManager.showSendMoney();
                break;
            case "Deposit":
                screenManager.showDepositWithdraw("WALLET");
                break;
            case "Withdraw":
                screenManager.showDepositWithdraw("WALLET");
                break;
            case "Savings":
                if (savingsAccountId != 0) screenManager.showSavings();
                else createSavingsAccount();
                break;
            case "Accounts":
                screenManager.showAccountOverview();
                break;
            case "History":
                screenManager.showTransactionHistory();
                break;
            case "Profile":
                screenManager.showProfile();
                break;
            case "Change PIN":
                screenManager.showPinChange();
                break;
            case "Loan":
                requestLoan();
                break;
        }
    }

    private void requestLoan() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Loan Request");
        dialog.setHeaderText("Request a small loan (max 50000)");
        dialog.setContentText("Amount:");
        dialog.showAndWait().ifPresent(amountStr -> {
            try {
                BigDecimal amount = new BigDecimal(amountStr);
                boolean approved = loanService.requestLoan(customerId, amount);
                if (approved) {
                    accountService.deposit(walletAccountId, amount, "LOAN_DISBURSEMENT");
                    showAlert("Loan Approved", "Amount " + amount + " credited to your wallet.");
                    refreshBalance();
                } else {
                    showAlert("Loan Denied", "Not eligible. Need more transaction history.");
                }
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });
    }

    private void createSavingsAccount() {
        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle("Open Savings Account");
        dialog.setHeaderText("Open a new savings account");
        dialog.setContentText("Initial deposit amount:");
        dialog.showAndWait().ifPresent(amountStr -> {
            try {
                BigDecimal amount = BigDecimal.ZERO;
                if (amountStr != null && !amountStr.trim().isEmpty()) {
                    amount = new BigDecimal(amountStr.trim());
                }
                accountService.createSavingsAccount(customerId, amount, 3, BigDecimal.valueOf(500));
                loadAccounts();
                updateAccountSummary();
                showAlert("Success", "Savings account created.");
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });
    }

    private void updateAccountSummary() {
        walletSummaryLabel.setText("Wallet Account: " + (walletBalance != null ? walletBalance.toString() : "0"));
        savingsSummaryLabel.setText(savingsAccountId != 0 ? "Savings Account: Active" : "Savings Account: Not created");
    }

    private void refreshBalance() {
        try {
            walletBalance = accountService.getBalance(walletAccountId);
            balanceText.setText(walletBalance.toString());
        } catch (Exception ignored) {
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public Parent getView() {
        return view;
    }
}