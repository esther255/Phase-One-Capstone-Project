package com.igirepay.LAB3_ui;

import com.igirepay.ScreenManager;
import com.igirepay.LAB1_model.SavingsAccount;
import com.igirepay.LAB1_service.AccountService;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;

public class SavingsController {
    private final ScreenManager screenManager;
    private final int savingsAccountId;
    private final AccountService accountService;
    private VBox view;

    public SavingsController(ScreenManager screenManager, int savingsAccountId,
                             AccountService accountService, int customerId, String customerName) {
        this.screenManager = screenManager;
        this.savingsAccountId = savingsAccountId;
        this.accountService = accountService;
        buildUI();
    }

    private void buildUI() {
        view = new VBox(15);
        view.setAlignment(Pos.CENTER);
        view.setStyle("-fx-padding: 20;");

        Label info = new Label();
        try {
            SavingsAccount acc = (SavingsAccount) accountService.getAccountById(savingsAccountId);
            info.setText("Balance: " + acc.getBalance() + "\nWithdrawals used: " + acc.getWithdrawalCount() +
                    "/" + acc.getWithdrawalLimit() + "\nFee per withdrawal: " + acc.getWithdrawalFee());
        } catch (Exception e) {
            info.setText("Error loading savings data: " + e.getMessage());
        }

        TextField amountField = new TextField();
        amountField.setPromptText("Amount to withdraw");

        Button withdrawBtn = new Button("Withdraw from Savings");
        withdrawBtn.setOnAction(e -> {
            try {
                BigDecimal amount = new BigDecimal(amountField.getText().trim());
                accountService.withdraw(savingsAccountId, amount, "Savings withdrawal");
                showAlert("Success", "Withdrawn " + amount + " from savings (fee applied).");
                screenManager.showDashboard();
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        Button deleteBtn = new Button("Delete Savings Account");
        deleteBtn.setDisable(true);
        try {
            SavingsAccount acc = (SavingsAccount) accountService.getAccountById(savingsAccountId);
            if (acc.getBalance().compareTo(BigDecimal.ZERO) == 0) {
                deleteBtn.setDisable(false);
            } else {
                deleteBtn.setText("Delete disabled until balance is zero");
            }
        } catch (Exception ignored) {
            deleteBtn.setText("Delete unavailable");
        }
        deleteBtn.setOnAction(e -> {
            try {
                accountService.deleteAccount(savingsAccountId);
                showAlert("Success", "Savings account deleted.");
                screenManager.showDashboard();
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        Button back = new Button("Back");
        back.setOnAction(e -> screenManager.showDashboard());

        view.getChildren().addAll(info, amountField, withdrawBtn, deleteBtn, back);
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public VBox getView() { return view; }
}