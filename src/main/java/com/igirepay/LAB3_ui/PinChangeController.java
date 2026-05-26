package com.igirepay.LAB3_ui;

import com.igirepay.ScreenManager;
import com.igirepay.LAB1_service.AuthService;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import java.util.Scanner;
public class PinChangeController {
    private final ScreenManager screenManager;
    private final int customerId;
    private final AuthService authService;
    private VBox view;

    public PinChangeController(ScreenManager screenManager, int customerId, AuthService authService) {
        this.screenManager = screenManager;
        this.customerId = customerId;
        this.authService = authService;
        buildUI();
    }

    private void buildUI() {
        view = new VBox(15);
        view.setAlignment(Pos.CENTER);
        view.setStyle("-fx-padding: 20;");

        PasswordField oldPinField = new PasswordField();
        oldPinField.setPromptText("Old PIN");
        oldPinField.setMaxWidth(250);

        PasswordField newPinField = new PasswordField();
        newPinField.setPromptText("New PIN (5 digits)");
        newPinField.setMaxWidth(250);

        PasswordField confirmPinField = new PasswordField();
        confirmPinField.setPromptText("Confirm New PIN");
        confirmPinField.setMaxWidth(250);

        Button changeBtn = new Button("Change PIN");
        changeBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        changeBtn.setOnAction(e -> {
            String oldPin = oldPinField.getText();
            String newPin = newPinField.getText();
            String confirmPin = confirmPinField.getText();

            if (oldPin.isEmpty() || newPin.isEmpty() || confirmPin.isEmpty()) {
                showAlert("Error", "All fields are required.");
                return;
            }
            if (!newPin.equals(confirmPin)) {
                showAlert("Error", "New PINs do not match.");
                return;
            }
            if (newPin.length() != 5 || !newPin.matches("\\d+")) {
                showAlert("Error", "PIN must be exactly 5 digits.");
                return;
            }
            try {
                authService.changePin(customerId, oldPin, newPin);
                showAlert("Success", "PIN changed successfully.");
                screenManager.showDashboard();
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> screenManager.showDashboard());

        view.getChildren().addAll(
                new Label("Change Your PIN"),
                oldPinField,
                newPinField,
                confirmPinField,
                changeBtn,
                backBtn
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