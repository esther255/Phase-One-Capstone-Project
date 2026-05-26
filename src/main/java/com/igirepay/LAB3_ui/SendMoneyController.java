package com.igirepay.LAB3_ui;

import com.igirepay.ScreenManager;
import com.igirepay.LAB1_service.TransferService;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.math.BigDecimal;

public class SendMoneyController {
    private final ScreenManager screenManager;
    private final int customerId;
    private final int walletAccountId;
    private final TransferService transferService;
    private VBox view;

    public SendMoneyController(ScreenManager screenManager, int customerId,
                               int walletAccountId, TransferService transferService) {
        this.screenManager = screenManager;
        this.customerId = customerId;
        this.walletAccountId = walletAccountId;
        this.transferService = transferService;
        buildUI();
    }

    private void buildUI() {
        view = new VBox(15);
        view.setAlignment(Pos.CENTER);
        view.setStyle("-fx-padding: 20;");

        TextField recipientPhone = new TextField();
        recipientPhone.setPromptText("Recipient phone number");

        TextField amountField = new TextField();
        amountField.setPromptText("Amount");

        TextArea description = new TextArea();
        description.setPromptText("Optional note");
        description.setMaxHeight(80);

        Button sendBtn = new Button("Send Money");
        sendBtn.setOnAction(e -> {
            try {
                String phone = recipientPhone.getText().trim();
                BigDecimal amount = new BigDecimal(amountField.getText().trim());
                String ref = transferService.sendMoney(walletAccountId, phone, amount, description.getText());
                showAlert("Success", "Transfer sent! Reference: " + ref);
                screenManager.showDashboard();
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        Button back = new Button("Back");
        back.setOnAction(e -> screenManager.showDashboard());

        view.getChildren().addAll(
                new Label("Send Money to Mobile Number"),
                recipientPhone, amountField, description, sendBtn, back
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