package com.igirepay.LAB3_ui;

import com.igirepay.ScreenManager;
import com.igirepay.LAB1_model.Customer;
import com.igirepay.LAB1_service.AuthService;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ProfileController {
    private final ScreenManager screenManager;
    private final int customerId;
    private final AuthService authService;
    private VBox view;

    public ProfileController(ScreenManager screenManager, int customerId, AuthService authService) {
        this.screenManager = screenManager;
        this.customerId = customerId;
        this.authService = authService;
        buildUI();
    }

    private void buildUI() {
        view = new VBox(15);
        view.setAlignment(Pos.CENTER);
        view.setStyle("-fx-padding: 20;");

        try {
            Customer customer = authService.getCustomerById(customerId);
            TextField nameField = new TextField(customer.getFullName());
            TextField emailField = new TextField(customer.getEmail());
            TextField phoneField = new TextField(customer.getPhoneNumber());
            phoneField.setEditable(false);

            Button updateBtn = new Button("Update Profile");
            updateBtn.setOnAction(e -> {
                try {
                    authService.updateCustomerInfo(customerId, nameField.getText(), emailField.getText());
                    showAlert("Success", "Profile updated.");
                    screenManager.showDashboard();
                } catch (Exception ex) {
                    showAlert("Error", ex.getMessage());
                }
            });

            Button back = new Button("Back");
            back.setOnAction(e -> screenManager.showDashboard());

            view.getChildren().addAll(
                    new Label("Your Profile"),
                    nameField, emailField, phoneField, updateBtn, back
            );
        } catch (Exception e) {
            view.getChildren().add(new Label("Error loading profile: " + e.getMessage()));
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public VBox getView() { return view; }
}