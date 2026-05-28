package com.igirepay.LAB3_ui;

import com.igirepay.ScreenManager;
import com.igirepay.LAB1_service.AccountService;
import com.igirepay.LAB1_service.TransferService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.math.BigDecimal;

public class SendMoneyController {

    private final ScreenManager  screenManager;
    private final int            customerId;
    private final int            walletAccountId;
    private final TransferService transferService;
    private final AccountService  accountService;
    private VBox view;

    public SendMoneyController(ScreenManager screenManager, int customerId,
                               int walletAccountId, TransferService transferService,
                               AccountService accountService) {
        this.screenManager    = screenManager;
        this.customerId       = customerId;
        this.walletAccountId  = walletAccountId;
        this.transferService  = transferService;
        this.accountService   = accountService;
        buildUI();
    }

    private void buildUI() {
        view = new VBox(18);
        view.setAlignment(Pos.TOP_CENTER);
        view.setPadding(new Insets(30));
        view.setStyle("-fx-background-color: #f4f6fb;");


        Label title = new Label("Send Money");
        title.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #1a237e;");


        VBox balanceCard = new VBox(4);
        balanceCard.setStyle("-fx-background-color: #1a237e; -fx-background-radius: 10;"
                + "-fx-padding: 15;");
        balanceCard.setMaxWidth(400);
        Label balLbl = new Label("Your Wallet Balance");
        balLbl.setStyle("-fx-text-fill: #b0bec5; -fx-font-size: 12;");
        Label balValue = new Label("Loading...");
        balValue.setStyle("-fx-text-fill: white; -fx-font-size: 22; -fx-font-weight: bold;");
        try {
            BigDecimal bal = accountService.getBalance(walletAccountId);
            balValue.setText(bal.toPlainString() + " RWF");
        } catch (Exception e) {
            balValue.setText("Unavailable");
        }
        balanceCard.getChildren().addAll(balLbl, balValue);


        Label recipientLbl = new Label("Recipient Phone Number");
        recipientLbl.setStyle("-fx-font-size: 13; -fx-text-fill: #333;");
        TextField recipientPhone = new TextField();
        recipientPhone.setPromptText("e.g. 0790079144");
        recipientPhone.setMaxWidth(400);
        recipientPhone.setStyle("-fx-font-size: 14; -fx-padding: 10;");

        Label amountLbl = new Label("Amount (RWF)");
        amountLbl.setStyle("-fx-font-size: 13; -fx-text-fill: #333;");
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");
        amountField.setMaxWidth(400);
        amountField.setStyle("-fx-font-size: 14; -fx-padding: 10;");

        Label noteLbl = new Label("Note (optional)");
        noteLbl.setStyle("-fx-font-size: 13; -fx-text-fill: #333;");
        TextField noteField = new TextField();
        noteField.setPromptText("What is this for?");
        noteField.setMaxWidth(400);
        noteField.setStyle("-fx-font-size: 14; -fx-padding: 10;");


        Text errorText = new Text();
        errorText.setStyle("-fx-fill: #c62828; -fx-font-size: 13;");


        Button sendBtn = new Button("Send Money →");
        sendBtn.setPrefWidth(400);
        sendBtn.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white;"
                + "-fx-font-size: 15; -fx-font-weight: bold; -fx-padding: 12;"
                + "-fx-background-radius: 8;");
        sendBtn.setOnAction(e -> {
            errorText.setText("");
            String phone  = recipientPhone.getText().trim();
            String rawAmt = amountField.getText().trim();
            String note   = noteField.getText().trim();


            if (phone.isEmpty()) {
                errorText.setText("Please enter the recipient's phone number.");
                return;
            }
            if (!phone.matches("\\d{9,12}")) {
                errorText.setText("Phone number must be 9–12 digits.");
                return;
            }
            if (rawAmt.isEmpty()) {
                errorText.setText("Please enter an amount.");
                return;
            }
            BigDecimal amount;
            try {
                amount = new BigDecimal(rawAmt);
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    errorText.setText("Amount must be greater than 0.");
                    return;
                }
            } catch (NumberFormatException nfe) {
                errorText.setText("Invalid amount. Enter a number.");
                return;
            }


            try {
                String ref = transferService.sendMoney(walletAccountId, phone, amount,
                        note.isEmpty() ? null : note);


                showReceipt(phone, amount, ref, note);
                screenManager.showDashboard();

            } catch (Exception ex) {
                errorText.setText(ex.getMessage());
            }
        });


        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setPrefWidth(400);
        backBtn.setStyle("-fx-background-color: #9e9e9e; -fx-text-fill: white;"
                + "-fx-font-size: 13; -fx-padding: 10; -fx-background-radius: 8;");
        backBtn.setOnAction(e -> screenManager.showDashboard());

        view.getChildren().addAll(
                title, balanceCard,
                recipientLbl, recipientPhone,
                amountLbl, amountField,
                noteLbl, noteField,
                errorText,
                sendBtn, backBtn
        );
    }


    private void showReceipt(String recipientPhone, BigDecimal amount, String ref, String note) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Transfer Successful");
        alert.setHeaderText("✔  Money Sent Successfully");
        String body = String.format(
                "To         : %s%n" +
                "Amount     : %s RWF%n" +
                "Reference  : %s%n" +
                (note != null && !note.isEmpty() ? "Note       : " + note + "%n" : "") +
                "%nKeep your reference ID for tracking.",
                recipientPhone, amount.toPlainString(), ref);
        alert.setContentText(body);
        alert.showAndWait();
    }

    public VBox getView() { return view; }
}
