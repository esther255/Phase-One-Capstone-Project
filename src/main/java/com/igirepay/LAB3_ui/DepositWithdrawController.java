package com.igirepay.LAB3_ui;

import com.igirepay.ScreenManager;
import com.igirepay.LAB1_service.AccountService;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.math.BigDecimal;

public class DepositWithdrawController {
    private final ScreenManager screenManager;
    private final int accountId;
    private final String accountType;
    private final AccountService accountService;
    private VBox view;
    private String mode = "DEPOSIT";

    public DepositWithdrawController(ScreenManager screenManager, int accountId, String accountType,
                                     AccountService accountService, int customerId, String customerName) {
        this.screenManager = screenManager;
        this.accountId = accountId;
        this.accountType = accountType;
        this.accountService = accountService;
        buildUI();
    }

    private void buildUI() {
        view = new VBox(15);
        view.setAlignment(Pos.CENTER);
        view.setStyle("-fx-padding: 20;");

        ToggleGroup group = new ToggleGroup();
        RadioButton depositRadio = new RadioButton("Deposit");
        RadioButton withdrawRadio = new RadioButton("Withdraw");
        depositRadio.setToggleGroup(group);
        withdrawRadio.setToggleGroup(group);
        depositRadio.setSelected(true);
        depositRadio.setOnAction(e -> mode = "DEPOSIT");
        withdrawRadio.setOnAction(e -> mode = "WITHDRAW");

        TextField amountField = new TextField();
        amountField.setPromptText("Amount");

        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            try {
                BigDecimal amount = new BigDecimal(amountField.getText().trim());
                if (mode.equals("DEPOSIT")) {
                    accountService.deposit(accountId, amount, "Cash In");
                } else {
                    accountService.withdraw(accountId, amount, "Cash Out");
                }
                showAlert("Success", mode + " of " + amount + " completed.");
                screenManager.showDashboard();
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        Button back = new Button("Back");
        back.setOnAction(e -> screenManager.showDashboard());

        view.getChildren().addAll(
                new Label(accountType + " Transaction"),
                depositRadio, withdrawRadio, amountField, submit, back
        );
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public VBox getView() { return view; }
}