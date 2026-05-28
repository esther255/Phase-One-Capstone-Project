package com.igirepay.LAB3_ui;

import com.igirepay.ScreenManager;
import com.igirepay.LAB1_model.SavingsAccount;
import com.igirepay.LAB1_service.AccountService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.math.BigDecimal;

public class SavingsController {

    private final ScreenManager screenManager;
    private final int savingsAccountId;
    private final AccountService accountService;
    private VBox view;

    // live labels updated after each action
    private Label balanceLabel;
    private Label withdrawalsLabel;

    public SavingsController(ScreenManager screenManager, int savingsAccountId,
                             AccountService accountService, int customerId, String customerName) {
        this.screenManager = screenManager;
        this.savingsAccountId = savingsAccountId;
        this.accountService = accountService;
        buildUI();
    }

    private void buildUI() {
        view = new VBox(18);
        view.setAlignment(Pos.TOP_CENTER);
        view.setPadding(new Insets(30));
        view.setStyle("-fx-background-color: #f4f6fb;");


        Label title = new Label("Savings Account");
        title.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #1a237e;");


        VBox infoCard = new VBox(8);
        infoCard.setStyle("-fx-background-color: white; -fx-background-radius: 10;"
                + "-fx-border-radius: 10; -fx-padding: 15; -fx-effect: dropshadow(gaussian,#ccc,6,0,0,2);");
        infoCard.setMaxWidth(400);

        balanceLabel = new Label();
        balanceLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        withdrawalsLabel = new Label();
        withdrawalsLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #555;");
        Label feeLabel = new Label();
        feeLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #555;");

        try {
            SavingsAccount acc = (SavingsAccount) accountService.getAccountById(savingsAccountId);
            balanceLabel.setText("Balance: " + acc.getBalance() + " RWF");
            withdrawalsLabel.setText("Withdrawals used: " + acc.getWithdrawalCount()
                    + " / " + acc.getWithdrawalLimit());
            feeLabel.setText("Fee per withdrawal: " + acc.getWithdrawalFee() + " RWF");
        } catch (Exception e) {
            balanceLabel.setText("Error loading account: " + e.getMessage());
        }

        infoCard.getChildren().addAll(balanceLabel, withdrawalsLabel, feeLabel);


        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount (RWF)");
        amountField.setMaxWidth(300);
        amountField.setStyle("-fx-font-size: 14; -fx-padding: 8;");


        Button depositBtn = new Button("Deposit to Savings");
        depositBtn.setPrefWidth(300);
        depositBtn.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white;"
                + "-fx-font-size: 14; -fx-padding: 10; -fx-background-radius: 8;");
        depositBtn.setOnAction(e -> {
            String raw = amountField.getText().trim();
            if (raw.isEmpty()) { showAlert("Input Required", "Please enter an amount."); return; }
            try {
                BigDecimal amount = new BigDecimal(raw);
                accountService.deposit(savingsAccountId, amount, "Savings deposit");
                amountField.clear();
                refreshInfo();
                showAlert("Success", "Deposited " + amount + " RWF to savings.");
            } catch (NumberFormatException nfe) {
                showAlert("Invalid Input", "Please enter a valid number.");
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });


        Button withdrawBtn = new Button("Withdraw from Savings");
        withdrawBtn.setPrefWidth(300);
        withdrawBtn.setStyle("-fx-background-color: #f57c00; -fx-text-fill: white;"
                + "-fx-font-size: 14; -fx-padding: 10; -fx-background-radius: 8;");
        withdrawBtn.setOnAction(e -> {
            String raw = amountField.getText().trim();
            if (raw.isEmpty()) { showAlert("Input Required", "Please enter an amount."); return; }
            try {
                BigDecimal amount = new BigDecimal(raw);
                accountService.withdraw(savingsAccountId, amount, "Savings withdrawal");
                amountField.clear();
                refreshInfo();
                showAlert("Success", "Withdrawn " + amount + " RWF from savings (fee applied).");
            } catch (NumberFormatException nfe) {
                showAlert("Invalid Input", "Please enter a valid number.");
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });


        Button deleteBtn = new Button("Close Savings Account");
        deleteBtn.setPrefWidth(300);
        deleteBtn.setStyle("-fx-background-color: #c62828; -fx-text-fill: white;"
                + "-fx-font-size: 13; -fx-padding: 8; -fx-background-radius: 8;");
        try {
            SavingsAccount acc = (SavingsAccount) accountService.getAccountById(savingsAccountId);
            if (acc.getBalance().compareTo(BigDecimal.ZERO) != 0) {
                deleteBtn.setDisable(true);
                deleteBtn.setText("Close Account (balance must be 0)");
            }
        } catch (Exception ignored) {}

        deleteBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to close your savings account?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Confirm Close");
            confirm.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.YES) {
                    try {
                        accountService.deleteAccount(savingsAccountId);
                        showAlert("Closed", "Savings account has been closed.");
                        screenManager.showDashboard();
                    } catch (Exception ex) {
                        showAlert("Error", ex.getMessage());
                    }
                }
            });
        });


        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setPrefWidth(300);
        backBtn.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white;"
                + "-fx-font-size: 14; -fx-padding: 10; -fx-background-radius: 8;");
        backBtn.setOnAction(e -> screenManager.showDashboard());

        view.getChildren().addAll(title, infoCard, amountField, depositBtn, withdrawBtn, deleteBtn, backBtn);
    }


    private void refreshInfo() {
        try {
            SavingsAccount acc = (SavingsAccount) accountService.getAccountById(savingsAccountId);
            balanceLabel.setText("Balance: " + acc.getBalance() + " RWF");
            withdrawalsLabel.setText("Withdrawals used: " + acc.getWithdrawalCount()
                    + " / " + acc.getWithdrawalLimit());
        } catch (Exception ignored) {}
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public VBox getView() { return view; }
}
