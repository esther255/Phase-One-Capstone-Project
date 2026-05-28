package com.igirepay.LAB3_ui;

import com.igirepay.ScreenManager;
import com.igirepay.LAB1_model.Account;
import com.igirepay.LAB1_service.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.math.BigDecimal;
import java.util.List;

public class DashboardController {

    private final ScreenManager      screenManager;
    private final int                customerId;
    private final String             fullName;
    private final AccountService     accountService;
    private final TransactionService transactionService;
    private final TransferService    transferService;
    private final LoanService        loanService;
    private final CSVExportService   csvExportService;

    private int        walletAccountId  = 0;
    private int        savingsAccountId = 0;
    private BigDecimal walletBalance    = BigDecimal.ZERO;
    private boolean    balanceVisible   = false;

    private Label balanceLabel;
    private Label savingsStatusLabel;

    public DashboardController(ScreenManager screenManager, int customerId, String fullName,
                               AccountService accountService, TransactionService transactionService,
                               TransferService transferService, LoanService loanService,
                               CSVExportService csvExportService) {
        this.screenManager      = screenManager;
        this.customerId         = customerId;
        this.fullName           = fullName;
        this.accountService     = accountService;
        this.transactionService = transactionService;
        this.transferService    = transferService;
        this.loanService        = loanService;
        this.csvExportService   = csvExportService;
        loadAccounts();
    }

    private void loadAccounts() {
        try {
            for (Account acc : accountService.getAccountsByCustomer(customerId)) {
                if ("WALLET".equals(acc.getAccountType())) {
                    walletAccountId = acc.getId();
                    walletBalance   = acc.getBalance() != null ? acc.getBalance() : BigDecimal.ZERO;
                } else if ("SAVINGS".equals(acc.getAccountType())) {
                    savingsAccountId = acc.getId();
                }
            }
        } catch (Exception ignored) {}
    }

    public Parent getView() {
        // ── Root: yellow top + white bottom ───────────────────
        VBox root = new VBox();
        root.setStyle("-fx-background-color: #FFCC00;");

        // ── HEADER ────────────────────────────────────────────
        VBox header = buildHeader();

        // ── WHITE BODY ────────────────────────────────────────
        VBox body = new VBox(0);
        body.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 24 24 0 0;");
        VBox.setVgrow(body, Priority.ALWAYS);

        // ── Quick actions ──────────────────────────────────────
        HBox quickActions = buildQuickActions();
        quickActions.setStyle("-fx-background-color: white; -fx-background-radius: 24 24 0 0;");
        quickActions.setPadding(new Insets(22, 16, 18, 16));

        Separator sep1 = new Separator();
        sep1.setPadding(new Insets(0, 16, 0, 16));

        // ── Services grid ──────────────────────────────────────
        VBox servicesSection = buildServicesSection();

        // ── Savings banner ─────────────────────────────────────
        HBox savingsBanner = buildSavingsBanner();

        // ── Bottom nav ─────────────────────────────────────────
        HBox bottomNav = buildBottomNav();
        bottomNav.setStyle("-fx-background-color: white; -fx-border-color: #eee; -fx-border-width: 1 0 0 0;");

        ScrollPane scroll = new ScrollPane(new VBox(0, quickActions, sep1, servicesSection, savingsBanner));
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        body.getChildren().addAll(scroll, bottomNav);
        root.getChildren().addAll(header, body);
        return root;
    }

    // =========================================================================
    //  HEADER  (yellow zone)
    // =========================================================================
    private VBox buildHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(18, 20, 24, 20));
        header.setStyle("-fx-background-color: #FFCC00;");

        // ── Top row: greeting + profile icon ──────────────────
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox greetBox = new VBox(2);
        Label hi = new Label("Hello,");
        hi.setStyle("-fx-font-size: 13; -fx-text-fill: #5a4a00;");
        Label name = new Label(fullName);
        name.setFont(Font.font("System", FontWeight.BOLD, 18));
        name.setStyle("-fx-text-fill: #1a1a1a;");
        greetBox.getChildren().addAll(hi, name);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button profileBtn = new Button(String.valueOf(fullName.charAt(0)).toUpperCase());
        profileBtn.setPrefSize(40, 40);
        profileBtn.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white;"
                + "-fx-font-weight: bold; -fx-font-size: 16; -fx-background-radius: 20; -fx-cursor: hand;");
        profileBtn.setOnAction(e -> screenManager.showProfile());
        topRow.getChildren().addAll(greetBox, spacer, profileBtn);

        // ── Balance card (or Activate Wallet prompt) ───────────
        VBox balCard = new VBox(6);
        balCard.setStyle("-fx-background-color: #1a237e; -fx-background-radius: 16; -fx-padding: 18; -fx-cursor: hand;");

        if (walletAccountId == 0) {
            // ── NO WALLET: show activation prompt ─────────────
            Label noWalletIcon = new Label("💳");
            noWalletIcon.setStyle("-fx-font-size: 28;");

            Label noWalletTitle = new Label("No Wallet Yet");
            noWalletTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
            noWalletTitle.setStyle("-fx-text-fill: white;");

            Label noWalletSub = new Label("Tap here to activate your wallet");
            noWalletSub.setStyle("-fx-text-fill: #90caf9; -fx-font-size: 13;");

            Button activateBtn = new Button("Activate Wallet →");
            activateBtn.setStyle("-fx-background-color: #FFCC00; -fx-text-fill: #1a1a1a;"
                    + "-fx-font-weight: bold; -fx-font-size: 13; -fx-background-radius: 20;"
                    + "-fx-padding: 8 18; -fx-cursor: hand;");
            activateBtn.setOnAction(e -> screenManager.showWallet());

            balCard.getChildren().addAll(noWalletIcon, noWalletTitle, noWalletSub, activateBtn);
            balCard.setOnMouseClicked(e -> screenManager.showWallet());

        } else {
            // ── HAS WALLET: show balance ───────────────────────
            Label balTitle = new Label("Wallet Balance");
            balTitle.setStyle("-fx-text-fill: #90caf9; -fx-font-size: 12;");

            balanceLabel = new Label("● ● ● ● ●");
            balanceLabel.setFont(Font.font("System", FontWeight.BOLD, 26));
            balanceLabel.setStyle("-fx-text-fill: white;");

            Button eyeBtn = new Button("👁 Show");
            eyeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #90caf9;"
                    + "-fx-font-size: 12; -fx-cursor: hand;");
            eyeBtn.setOnAction(e -> {
                balanceVisible = !balanceVisible;
                balanceLabel.setText(balanceVisible
                        ? walletBalance.toPlainString() + " RWF"
                        : "● ● ● ● ●");
                eyeBtn.setText(balanceVisible ? "🙈 Hide" : "👁 Show");
            });

            savingsStatusLabel = new Label(savingsAccountId != 0
                    ? "Savings: Active"
                    : "Savings: Not activated");
            savingsStatusLabel.setStyle("-fx-text-fill: #7986cb; -fx-font-size: 11;");

            balCard.getChildren().addAll(balTitle, balanceLabel, eyeBtn, savingsStatusLabel);
        }

        header.getChildren().addAll(topRow, balCard);
        return header;
    }

    // =========================================================================
    //  QUICK ACTIONS  (Send, Deposit, Withdraw, History)
    // =========================================================================
    private HBox buildQuickActions() {
        HBox row = new HBox(0);
        row.setAlignment(Pos.CENTER);

        row.getChildren().addAll(
            buildQuickBtn("➤",  "Send",     () -> navigate("Send Money")),
            buildQuickBtn("⬇",  "Deposit",  () -> navigate("Deposit")),
            buildQuickBtn("⬆",  "Withdraw", () -> navigate("Withdraw")),
            buildQuickBtn("📋", "History",  () -> navigate("History"))
        );
        return row;
    }

    private VBox buildQuickBtn(String icon, String label, Runnable action) {
        Button btn = new Button(icon);
        btn.setPrefSize(52, 52);
        btn.setStyle("-fx-background-color: #FFCC00; -fx-text-fill: #1a1a1a;"
                + "-fx-font-size: 20; -fx-background-radius: 26; -fx-cursor: hand;");
        btn.setOnAction(e -> action.run());

        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 11; -fx-text-fill: #444;");

        VBox box = new VBox(6, btn, lbl);
        box.setAlignment(Pos.CENTER);
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    // =========================================================================
    //  SERVICES GRID
    // =========================================================================
    private VBox buildServicesSection() {
        VBox section = new VBox(12);
        section.setPadding(new Insets(18, 16, 10, 16));
        section.setStyle("-fx-background-color: #f5f5f5;");

        Label title = new Label("Services");
        title.setFont(Font.font("System", FontWeight.BOLD, 15));
        title.setStyle("-fx-text-fill: #1a1a1a;");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);

        String[][] services = {
            {"💳", "Wallet",      "Manage wallet"},
            {"🏦", "Savings",     "Save & earn"},
            {"👤", "Profile",     "My account"},
            {"🔑", "Change PIN",  "Security"},
            {"💰", "Loan",        "Quick loan"},
            {"📊", "Accounts",    "All accounts"}
        };

        int col = 0, row = 0;
        for (String[] s : services) {
            grid.add(buildServiceCard(s[0], s[1], s[2]), col, row);
            col++;
            if (col == 3) { col = 0; row++; }
        }

        section.getChildren().addAll(title, grid);
        return section;
    }

    private VBox buildServiceCard(String icon, String label, String sub) {
        VBox card = new VBox(4);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 14; -fx-cursor: hand;");
        card.setPrefWidth(140);

        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 22;");
        Label nameLbl = new Label(label);
        nameLbl.setFont(Font.font("System", FontWeight.BOLD, 13));
        nameLbl.setStyle("-fx-text-fill: #1a1a1a;");
        Label subLbl = new Label(sub);
        subLbl.setStyle("-fx-font-size: 10; -fx-text-fill: #888;");

        card.getChildren().addAll(iconLbl, nameLbl, subLbl);
        card.setOnMouseClicked(e -> navigate(label));

        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: #fff9e6; -fx-background-radius: 14; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 14; -fx-cursor: hand;"));

        return card;
    }

    // =========================================================================
    //  SAVINGS BANNER
    // =========================================================================
    private HBox buildSavingsBanner() {
        HBox banner = new HBox(14);
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.setPadding(new Insets(16, 16, 20, 16));
        banner.setStyle("-fx-background-color: #f5f5f5;");

        VBox inner = new VBox(4);
        inner.setAlignment(Pos.CENTER_LEFT);
        inner.setPadding(new Insets(16));
        inner.setStyle("-fx-background-color: #1a237e; -fx-background-radius: 14;");
        HBox.setHgrow(inner, Priority.ALWAYS);

        Label t = new Label(savingsAccountId != 0 ? "Savings Account Active" : "Open a Savings Account");
        t.setFont(Font.font("System", FontWeight.BOLD, 14));
        t.setStyle("-fx-text-fill: white;");

        Label s = new Label(savingsAccountId != 0
                ? "Tap to manage your savings"
                : "Save money and earn interest");
        s.setStyle("-fx-text-fill: #90caf9; -fx-font-size: 12;");

        Button actionBtn = new Button(savingsAccountId != 0 ? "Manage →" : "Activate →");
        actionBtn.setStyle("-fx-background-color: #FFCC00; -fx-text-fill: #1a1a1a;"
                + "-fx-font-weight: bold; -fx-font-size: 12; -fx-background-radius: 20;"
                + "-fx-padding: 6 14; -fx-cursor: hand;");
        actionBtn.setOnAction(e -> navigate("Savings"));

        inner.getChildren().addAll(t, s, actionBtn);
        banner.getChildren().add(inner);
        return banner;
    }

    // =========================================================================
    //  BOTTOM NAV
    // =========================================================================
    private HBox buildBottomNav() {
        HBox nav = new HBox(0);
        nav.setAlignment(Pos.CENTER);
        nav.setPadding(new Insets(10, 0, 10, 0));

        nav.getChildren().addAll(
            buildNavBtn("🏠", "Home",    true,  () -> screenManager.showDashboard()),
            buildNavBtn("💳", "Wallet",  false, () -> screenManager.showWallet()),
            buildNavBtn("📋", "History", false, () -> screenManager.showTransactionHistory()),
            buildNavBtn("👤", "Profile", false, () -> screenManager.showProfile())
        );
        return nav;
    }

    private VBox buildNavBtn(String icon, String label, boolean active, Runnable action) {
        Button btn = new Button(icon);
        btn.setStyle("-fx-background-color: transparent; -fx-font-size: 20; -fx-cursor: hand;");
        btn.setOnAction(e -> action.run());

        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 10; -fx-text-fill: " + (active ? "#FFCC00" : "#aaa") + ";");

        VBox box = new VBox(2, btn, lbl);
        box.setAlignment(Pos.CENTER);
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    // =========================================================================
    //  NAVIGATION
    // =========================================================================
    private void navigate(String action) {
        switch (action) {
            case "Send Money":
                if (walletAccountId != 0) screenManager.showSendMoney();
                else screenManager.showWallet();
                break;
            case "Deposit":
                if (walletAccountId != 0) screenManager.showDepositWithdraw("WALLET");
                else screenManager.showWallet();
                break;
            case "Withdraw":
                if (walletAccountId != 0) screenManager.showWithdraw("WALLET");
                else screenManager.showWallet();
                break;
            case "Savings":
                if (savingsAccountId != 0) screenManager.showSavings();
                else openSavings();
                break;
            case "Wallet":     screenManager.showWallet();            break;
            case "Accounts":   screenManager.showAccountOverview();   break;
            case "History":    screenManager.showTransactionHistory(); break;
            case "Profile":    screenManager.showProfile();           break;
            case "Change PIN": screenManager.showPinChange();         break;
            case "Loan":       requestLoan();                         break;
        }
    }

    private void openSavings() {
        TextInputDialog dlg = new TextInputDialog("0");
        dlg.setTitle("Open Savings");
        dlg.setHeaderText(null);
        dlg.setContentText("Initial deposit (RWF):");
        dlg.showAndWait().ifPresent(v -> {
            try {
                BigDecimal amt = v.trim().isEmpty() ? BigDecimal.ZERO : new BigDecimal(v.trim());
                accountService.createSavingsAccount(customerId, amt, 3, BigDecimal.valueOf(500));
                screenManager.showDashboard();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
            }
        });
    }

    private void requestLoan() {
        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Quick Loan");
        dlg.setHeaderText(null);
        dlg.setContentText("Amount (max 50,000 RWF):");
        dlg.showAndWait().ifPresent(v -> {
            try {
                BigDecimal amt = new BigDecimal(v.trim());
                boolean ok = loanService.requestLoan(customerId, amt);
                if (ok) {
                    accountService.deposit(walletAccountId, amt, "LOAN_DISBURSEMENT");
                    new Alert(Alert.AlertType.INFORMATION,
                            amt.toPlainString() + " RWF credited to your wallet.").showAndWait();
                    screenManager.showDashboard();
                } else {
                    new Alert(Alert.AlertType.WARNING,
                            "Not eligible. Need at least 3 transactions in the last 30 days.").showAndWait();
                }
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
            }
        });
    }
}
