package com.igirepay.LAB3_ui;

import com.igirepay.LAB1_service.AuthService;
import com.igirepay.ScreenManager;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class RegisterController {
    private final ScreenManager screenManager;
    private final AuthService authService;
    private VBox view;
    private TextField fullNameField;
    private TextField emailField;
    private TextField phoneField;
    private PasswordField pinField;
    private PasswordField confirmPinField;
    private Text errorLabel;

    public RegisterController(ScreenManager screenManager, AuthService authService) {
        this.screenManager = screenManager;
        this.authService = authService;
        buildUI();
    }

    private void buildUI() {
        view = new VBox(15);
        view.setAlignment(Pos.CENTER);
        view.setStyle("-fx-padding: 30; -fx-background-color: #f9f9f9;");

        Label title = new Label("Create IgirePay Account");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        fullNameField = new TextField();
        fullNameField.setPromptText("Full name");
        fullNameField.setMaxWidth(280);

        emailField = new TextField();
        emailField.setPromptText("Email address");
        emailField.setMaxWidth(280);

        phoneField = new TextField();
        phoneField.setPromptText("Phone number (e.g. 0790079144)");
        phoneField.setMaxWidth(280);

        pinField = new PasswordField();
        pinField.setPromptText("5-digit PIN");
        pinField.setMaxWidth(280);

        confirmPinField = new PasswordField();
        confirmPinField.setPromptText("Confirm PIN");
        confirmPinField.setMaxWidth(280);

        errorLabel = new Text();
        errorLabel.setStyle("-fx-fill: red;");

        Button registerBtn = new Button("Register");
        registerBtn.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-size: 16;");
        registerBtn.setOnAction(e -> doRegister());

        Button backBtn = new Button("Back to Login");
        backBtn.setStyle("-fx-background-color: #9e9e9e; -fx-text-fill: white; -fx-font-size: 14;");
        backBtn.setOnAction(e -> screenManager.showLoginScreen());

        view.getChildren().addAll(title, fullNameField, emailField, phoneField, pinField, confirmPinField, registerBtn, backBtn, errorLabel);
    }

    private void doRegister() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String pin = pinField.getText().trim();
        String confirmPin = confirmPinField.getText().trim();


        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || pin.isEmpty() || confirmPin.isEmpty()) {
            errorLabel.setText("All fields are required.");
            errorLabel.setStyle("-fx-fill: red;");
            return;
        }
        if (!pin.equals(confirmPin)) {
            errorLabel.setText("PINs do not match.");
            errorLabel.setStyle("-fx-fill: red;");
            return;
        }
        if (!pin.matches("\\d{5}")) {
            errorLabel.setText("PIN must be exactly 5 numeric digits.");
            errorLabel.setStyle("-fx-fill: red;");
            return;
        }
        if (!phone.matches("\\d{9,12}")) {
            errorLabel.setText("Phone number must contain only digits (9-12 digits).");
            errorLabel.setStyle("-fx-fill: red;");
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errorLabel.setText("Invalid email format.");
            errorLabel.setStyle("-fx-fill: red;");
            return;
        }

        try {
            authService.registerCustomer(fullName, email, phone, pin);
            errorLabel.setStyle("-fx-fill: green;");
            errorLabel.setText("Registration successful! Redirecting to login...");


            Button registerBtn = (Button) view.getChildren().get(view.getChildren().size() - 3);
            registerBtn.setDisable(true);


            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> screenManager.showLoginScreen());
                } catch (InterruptedException ignored) {}
            }).start();

        } catch (Exception ex) {
            errorLabel.setStyle("-fx-fill: red;");
            errorLabel.setText(ex.getMessage());
        }
    }

    public VBox getView() {
        return view;
    }
}