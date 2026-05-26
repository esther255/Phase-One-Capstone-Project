package com.igirepay.LAB3_ui;

import com.igirepay.ScreenManager;
import com.igirepay.LAB1_model.Transaction;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class ReceiptController {
    private final ScreenManager screenManager;
    private final Transaction transaction;
    private final String message;
    private VBox view;

    public ReceiptController(ScreenManager screenManager, Transaction transaction,
                             String message, int customerId, String customerName) {
        this.screenManager = screenManager;
        this.transaction = transaction;
        this.message = message;
        buildUI();
    }

    private void buildUI() {
        view = new VBox(10);
        view.setAlignment(Pos.CENTER);
        view.setStyle("-fx-padding: 30; -fx-background-color: #f9f9f9;");
        Label title = new Label("Transaction Receipt");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
        TextArea receipt = new TextArea();
        receipt.setEditable(false);
        receipt.setPrefRowCount(12);
        StringBuilder sb = new StringBuilder();
        sb.append("Reference: ").append(transaction.getReferenceId()).append("\n");
        sb.append("Type: ").append(transaction.getType()).append("\n");
        sb.append("Amount: ").append(transaction.getAmount()).append("\n");
        sb.append("Date: ").append(transaction.getCreatedAt()).append("\n");
        if (transaction.getDescription() != null)
            sb.append("Note: ").append(transaction.getDescription()).append("\n");
        sb.append("\n").append(message);
        receipt.setText(sb.toString());

        Button closeBtn = new Button("Back to Dashboard");
        closeBtn.setOnAction(e -> screenManager.showDashboard());
        view.getChildren().addAll(title, receipt, closeBtn);
    }

    public VBox getView() { return view; }
}