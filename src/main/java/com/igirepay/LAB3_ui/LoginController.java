package com.igirepay.LAB3_ui;

import com.igirepay.ScreenManager;
import com.igirepay.LAB1_service.AuthService;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class LoginController {
    private final ScreenManager screenManager;
    private final AuthService authService;
    private VBox view;
    private TextField phoneField;
    private PasswordField pinField;
    private Button[] digitButtons;
    private Text errorLabel;
    private int pinLength = 0;

    public LoginController(ScreenManager screenManager, AuthService authService) {
        this.screenManager = screenManager;
        this.authService = authService;
        buildUI();
    }

    private void buildUI() {
        view = new VBox(20);
        view.setAlignment(Pos.CENTER);
        view.setStyle("-fx-padding: 30; -fx-background-color: #f0f0f0;");

        phoneField = new TextField();
        phoneField.setPromptText("Phone number (e.g., 0788123456)");
        phoneField.setMaxWidth(250);

        pinField = new PasswordField();
        pinField.setPromptText("Enter 5-digit PIN");
        pinField.setMaxWidth(150);
        pinField.setEditable(false);

        errorLabel = new Text();
        errorLabel.setStyle("-fx-fill: red;");

        GridPane keypad = new GridPane();
        keypad.setHgap(10);
        keypad.setVgap(10);
        keypad.setAlignment(Pos.CENTER);
        digitButtons = new Button[10];
        for (int i = 0; i <= 9; i++) {
            final int digit = i;
            Button btn = new Button(String.valueOf(digit));
            btn.setPrefSize(60, 60);
            btn.setOnAction(e -> appendPinDigit(digit));
            digitButtons[i] = btn;
            int row = i / 3;
            int col = i % 3;
            if (i == 0) { row = 3; col = 1; }
            keypad.add(btn, col, row);
        }
        Button clearBtn = new Button("⌫");
        clearBtn.setPrefSize(60, 60);
        clearBtn.setOnAction(e -> removeLastDigit());
        keypad.add(clearBtn, 2, 3);

        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16;");
        loginBtn.setOnAction(e -> doLogin());

        Button registerBtn = new Button("Register");
        registerBtn.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-size: 16;");
        registerBtn.setOnAction(e -> screenManager.showRegisterScreen());

        view.getChildren().addAll(
                new Label("Welcome to IgirePay"), phoneField,
                new Label("Enter PIN:"), pinField, keypad, loginBtn, registerBtn, errorLabel
        );
    }

    private void appendPinDigit(int digit) {
        if (pinLength < 5) {
            pinField.appendText(String.valueOf(digit));
            pinLength++;
            if (pinLength == 5) doLogin();
        }
    }

    private void removeLastDigit() {
        if (pinLength > 0) {
            String current = pinField.getText();
            pinField.setText(current.substring(0, current.length() - 1));
            pinLength--;
        }
    }

    private void doLogin() {
        String phone = phoneField.getText().trim();
        String pin = pinField.getText();
        if (phone.isEmpty() || pin.length() != 5) {
            errorLabel.setText("Enter phone and 5-digit PIN");
            return;
        }
        try {
            var customer = authService.login(phone, pin);
            screenManager.setCurrentCustomer(customer.getId(), customer.getFullName());
            screenManager.showDashboard();
        } catch (Exception ex) {
            errorLabel.setText(ex.getMessage());
            pinField.clear();
            pinLength = 0;
        }
    }

    public VBox getView() { return view; }
}