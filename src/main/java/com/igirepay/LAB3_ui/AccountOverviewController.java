package com.igirepay.LAB3_ui;

import com.igirepay.ScreenManager;
import com.igirepay.LAB1_model.Account;
import com.igirepay.LAB1_model.SavingsAccount;
import com.igirepay.LAB1_service.AccountService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

public class AccountOverviewController {
    private final ScreenManager screenManager;
    private final int customerId;
    private final AccountService accountService;
    private VBox view;

    public AccountOverviewController(ScreenManager screenManager, int customerId, AccountService accountService) {
        this.screenManager = screenManager;
        this.customerId = customerId;
        this.accountService = accountService;
        buildUI();
    }

    private void buildUI() {
        view = new VBox(15);
        view.setPadding(new Insets(20));
        view.setStyle("-fx-background-color: #f1f8e9;");
        view.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Your Accounts");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        TableView<Account> table = new TableView<>();
        table.setPrefHeight(380);

        TableColumn<Account, Integer> idCol = new TableColumn<>("Account ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(90);

        TableColumn<Account, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        typeCol.setPrefWidth(120);

        TableColumn<Account, String> balanceCol = new TableColumn<>("Balance");
        balanceCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getBalance().toString()));
        balanceCol.setPrefWidth(120);

        TableColumn<Account, String> createdCol = new TableColumn<>("Created At");
        createdCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCreatedAt().toString()));
        createdCol.setPrefWidth(220);

        TableColumn<Account, String> extraCol = new TableColumn<>("Details");
        extraCol.setCellValueFactory(cell -> {
            Account account = cell.getValue();
            if (account instanceof SavingsAccount) {
                SavingsAccount savings = (SavingsAccount) account;
                return new SimpleStringProperty(String.format("Withdrawals: %d/%d, Fee: %s",
                        savings.getWithdrawalCount(), savings.getWithdrawalLimit(), savings.getWithdrawalFee()));
            }
            return new SimpleStringProperty("Wallet account");
        });
        extraCol.setPrefWidth(260);

        table.getColumns().addAll(idCol, typeCol, balanceCol, createdCol, extraCol);
        table.setItems(loadAccounts());

        Button back = new Button("Back to Dashboard");
        back.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-size: 14;");
        back.setOnAction(e -> screenManager.showDashboard());

        view.getChildren().addAll(title, table, back);
    }

    private ObservableList<Account> loadAccounts() {
        try {
            List<Account> accounts = accountService.getAccountsByCustomer(customerId);
            return FXCollections.observableArrayList(accounts.stream().collect(Collectors.toList()));
        } catch (Exception e) {
            return FXCollections.observableArrayList();
        }
    }

    public VBox getView() {
        return view;
    }
}
