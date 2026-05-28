package com.igirepay;

import com.igirepay.LAB1_service.*;
import com.igirepay.LAB3_ui.*;
import com.igirepay.LAB1_model.Transaction;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScreenManager {
    private final Stage stage;
    private final AuthService authService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final TransferService transferService;
    private final LoanService loanService;
    private final CSVExportService csvExportService;


    private int currentCustomerId;
    private String currentCustomerName;

    public ScreenManager(Stage stage, AuthService authService, AccountService accountService,
                         TransactionService transactionService, TransferService transferService,
                         LoanService loanService, CSVExportService csvExportService) {
        this.stage = stage;
        this.authService = authService;
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.transferService = transferService;
        this.loanService = loanService;
        this.csvExportService = csvExportService;
    }

    public void setCurrentCustomer(int customerId, String name) {
        this.currentCustomerId = customerId;
        this.currentCustomerName = name;
    }

    public void showLoginScreen() {
        LoginController login = new LoginController(this, authService);
        stage.setScene(new Scene(login.getView(), 400, 650));
    }

    public void showRegisterScreen() {
        RegisterController register = new RegisterController(this, authService);
        stage.setScene(new Scene(register.getView(), 400, 700));
    }

    public void showDashboard() {
        DashboardController dashboard = new DashboardController(this, currentCustomerId, currentCustomerName,
                accountService, transactionService, transferService, loanService, csvExportService);
        stage.setScene(new Scene(dashboard.getView(), 800, 600));
    }

    public void showWallet() {
        WalletController wallet = new WalletController(this, currentCustomerId, accountService);
        stage.setScene(new Scene(wallet.getView(), 520, 600));
    }

    public void showSendMoney() {
        int walletId = getWalletAccountId();
        if (walletId == -1) {
            showErrorDialog("No Wallet Account",
                    "You don't have a wallet account yet.\nGo to the dashboard and click 'Send Money' to create one.");
            return;
        }
        SendMoneyController send = new SendMoneyController(
                this, currentCustomerId, walletId, transferService, accountService);
        stage.setScene(new Scene(send.getView(), 500, 620));
    }

    public void showDepositWithdraw(String accountType) {
        int accountId = (accountType.equals("SAVINGS")) ? getSavingsAccountId() : getWalletAccountId();
        if (accountId == -1) {
            showErrorDialog("Account Not Found", "The selected account type is not available for your customer profile.");
            return;
        }
        DepositWithdrawController dw = new DepositWithdrawController(this, accountId, accountType, "DEPOSIT", accountService, currentCustomerId, currentCustomerName);
        stage.setScene(new Scene(dw.getView(), 450, 500));
    }

    public void showWithdraw(String accountType) {
        int accountId = (accountType.equals("SAVINGS")) ? getSavingsAccountId() : getWalletAccountId();
        if (accountId == -1) {
            showErrorDialog("Account Not Found", "The selected account type is not available for your customer profile.");
            return;
        }
        DepositWithdrawController dw = new DepositWithdrawController(this, accountId, accountType, "WITHDRAW", accountService, currentCustomerId, currentCustomerName);
        stage.setScene(new Scene(dw.getView(), 450, 500));
    }

    public void showTransactionHistory() {
        TransactionHistoryController th = new TransactionHistoryController(this, currentCustomerId, transactionService, csvExportService);
        stage.setScene(new Scene(th.getView(), 900, 600));
    }

    public void showSavings() {
        int savingsId = getSavingsAccountId();
        if (savingsId == -1) {
            showErrorDialog("No Savings Account", "You don't have a savings account yet.");
            return;
        }
        SavingsController savings = new SavingsController(this, savingsId, accountService, currentCustomerId, currentCustomerName);
        stage.setScene(new Scene(savings.getView(), 500, 550));
    }

    public void showAccountOverview() {
        AccountOverviewController overview = new AccountOverviewController(this, currentCustomerId, accountService);
        stage.setScene(new Scene(overview.getView(), 800, 500));
    }

    public void showProfile() {
        ProfileController profile = new ProfileController(this, currentCustomerId, authService);
        stage.setScene(new Scene(profile.getView(), 500, 600));
    }

    public void showPinChange() {
        PinChangeController pinChange = new PinChangeController(this, currentCustomerId, authService);
        stage.setScene(new Scene(pinChange.getView(), 450, 500));
    }

    public void showReceipt(Transaction transaction, String message) {
        ReceiptController receipt = new ReceiptController(this, transaction, message, currentCustomerId, currentCustomerName);
        stage.setScene(new Scene(receipt.getView(), 500, 600));
    }

    private int getWalletAccountId() {
        try {
            return accountService.getAccountsByCustomer(currentCustomerId).stream()
                    .filter(a -> a.getAccountType().equals("WALLET"))
                    .findFirst()
                    .map(a -> a.getId())
                    .orElse(-1);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private int getSavingsAccountId() {
        try {
            return accountService.getAccountsByCustomer(currentCustomerId).stream()
                    .filter(a -> a.getAccountType().equals("SAVINGS"))
                    .findFirst()
                    .map(a -> a.getId())
                    .orElse(-1);
        } catch (Exception e) {
            return -1;
        }
    }

    private void showErrorDialog(String title, String msg) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}