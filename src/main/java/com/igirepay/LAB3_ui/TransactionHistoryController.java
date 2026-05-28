package com.igirepay.LAB3_ui;

import com.igirepay.ScreenManager;
import com.igirepay.LAB1_model.Transaction;
import com.igirepay.LAB1_service.CSVExportService;
import com.igirepay.LAB1_service.TransactionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.List;
public class TransactionHistoryController {
    private final ScreenManager screenManager;
    private final int customerId;
    private final TransactionService transactionService;
    private final CSVExportService csvExportService;
    private VBox view;
    private TableView<Transaction> table;

    public TransactionHistoryController(ScreenManager screenManager, int customerId,
                                        TransactionService transactionService, CSVExportService csvExportService) {
        this.screenManager = screenManager;
        this.customerId = customerId;
        this.transactionService = transactionService;
        this.csvExportService = csvExportService;
        buildUI();
        loadData();
    }

    private void buildUI() {
        view = new VBox(10);
        view.setStyle("-fx-padding: 20;");
        table = new TableView<>();
        TableColumn<Transaction, String> refCol = new TableColumn<>("Reference");
        refCol.setCellValueFactory(new PropertyValueFactory<>("referenceId"));
        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Transaction, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        table.getColumns().addAll(refCol, typeCol, amountCol, dateCol);
        table.setPrefHeight(400);

        Button exportBtn = new Button("Export to CSV");
        exportBtn.setOnAction(e -> exportCSV());
        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> screenManager.showDashboard());

        view.getChildren().addAll(new Label("Transaction History"), table, exportBtn, backBtn);
    }

    private void loadData() {
        try {
            List<Transaction> list = transactionService.getTransactionHistory(customerId);
            ObservableList<Transaction> data = FXCollections.observableArrayList(list);
            table.setItems(data);
        } catch (Exception ex) {
            showAlert("Error", ex.getMessage());
        }
    }

    private void exportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Transaction History");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                List<Transaction> transactions = transactionService.getTransactionHistory(customerId);
                csvExportService.exportTransactions(transactions, file.getAbsolutePath());
                showAlert("Export Successful", "File saved to " + file.getAbsolutePath());
            } catch (Exception ex) {
                showAlert("Export Failed", ex.getMessage());
            }
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