package com.igirepay.LAB3_ui;

import com.igirepay.ScreenManager;
import com.igirepay.LAB1_service.AccountService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;

public class DepositWithdrawController {

    private final ScreenManager  screenManager;
    private final int            accountId;
    private final String         accountType;
    private final AccountService accountService;
    private       String         mode;   // "DEPOSIT" or "WITHDRAW"
    private VBox view;

    /**
     * @param initialMode "DEPOSIT" or "WITHDRAW" – pre-selects the radio button
     */
    public DepositWithdrawController(ScreenManager screenManager, int accountId, String accountType,
                                     String initialMode, AccountService accountService,
                                     int customerId, String customerName) {
        this.screenManager  = screenManager;
        this.accountId      = accountId;
        this.accountType    = accountType;
        this.mode           = initialMode != null ? initialMode : "DEPOSIT";
        this.accountService = accountService;
        buildUI();
    }

    private void buildUI() {
        view = new VBox(15);
        view.setAlignment(Pos.CENTER);
        view.setPadding(new Insets(30));
        view.setStyle("-fx-background-color: #f5f5f5;");

        Label title = new Label(accountType + " – " + mode.charAt(0) + mode.substring(1).toLowerCase());
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #1a237e;");

        // ── Mode toggle ────────────────────────────────────────
        ToggleGroup group = new ToggleGroup();
        RadioButton depositRadio  = new RadioButton("Deposit");
        RadioButton withdrawRadio = new RadioButton("Withdraw");
        depositRadio.setToggleGroup(group);
        withdrawRadio.setToggleGroup(group);
        depositRadio.setSelected("DEPOSIT".equals(mode));
        withdrawRadio.setSelected("WITHDRAW".equals(mode));
        depositRadio.setOnAction(e  -> { mode = "DEPOSIT";  title.setText(accountType + " – Deposit"); });
        withdrawRadio.setOnAction(e -> { mode = "WITHDRAW"; title.setText(accountType + " – Withdraw"); });

        // ── Amount ─────────────────────────────────────────────
        TextField amountField = new TextField();
        amountField.setPromptText("Amount (RWF)");
        amountField.setMaxWidth(280);
        amountField.setStyle("-fx-font-size: 14; -fx-padding: 8;");

        // ── Submit ─────────────────────────────────────────────
        Button submitBtn = new Button("Submit");
        submitBtn.setPrefWidth(280);
        submitBtn.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white;"
                + "-fx-font-size: 14; -fx-padding: 10; -fx-background-radius: 8;");
        submitBtn.setOnAction(e -> {
            String raw = amountField.getText().trim();
            if (raw.isEmpty()) { showAlert("Input Required", "Please enter an amount."); return; }
            try {
                BigDecimal amount = new BigDecimal(raw);
                if (mode.equals("DEPOSIT")) {
                    accountService.deposit(accountId, amount, "Cash In");
                    showAlert("Success", "Deposited " + amount + " RWF successfully.");
                } else {
                    accountService.withdraw(accountId, amount, "Cash Out");
                    showAlert("Success", "Withdrawn " + amount + " RWF successfully.");
                }
                screenManager.showDashboard();
            } catch (NumberFormatException nfe) {
                showAlert("Invalid Input", "Please enter a valid number.");
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        // ── Back ───────────────────────────────────────────────
        Button backBtn = new Button("← Back");
        backBtn.setPrefWidth(280);
        backBtn.setStyle("-fx-background-color: #9e9e9e; -fx-text-fill: white;"
                + "-fx-font-size: 13; -fx-padding: 8; -fx-background-radius: 8;");
        backBtn.setOnAction(e -> screenManager.showDashboard());

        view.getChildren().addAll(title, depositRadio, withdrawRadio, amountField, submitBtn, backBtn);
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
