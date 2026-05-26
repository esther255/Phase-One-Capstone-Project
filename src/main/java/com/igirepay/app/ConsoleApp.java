package com.igirepay.app;

import com.igirepay.LAB1_service.*;
import com.igirepay.LAB2_dao.*;
import com.igirepay.LAB1_model.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    private static Scanner sc = new Scanner(System.in);
    private static AuthService authService;
    private static AccountService accountService;
    private static TransactionService transactionService;
    private static TransferService transferService;
    private static LoanService loanService;
    private static CSVExportService csvExportService;
    private static Customer currentCustomer;

    public static void main(String[] args) {
        try {
            initServices();
            mainMenu();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void initServices() throws Exception {
        CustomerDAO customerDAO = new CustomerDAOImpl();
        AccountDAO accountDAO = new AccountDAOImpl();
        TransactionDAO transactionDAO = new TransactionDAOImpl();
        ProcessedRequestDAO processedRequestDAO = new ProcessedRequestDAOImpl();

        authService = new AuthService(customerDAO);
        transactionService = new TransactionService(transactionDAO, processedRequestDAO);
        accountService = new AccountService(accountDAO, transactionService);
        transferService = new TransferService(accountDAO, customerDAO, accountService, transactionService, transactionDAO);
        loanService = new LoanService(transactionDAO);
        csvExportService = new CSVExportService();
    }

    private static void mainMenu() {
        while (true) {
            System.out.println("\n===== IGIREPAY CONSOLE =====");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            int choice = readInt();
            if (choice == 1) login();
            else if (choice == 2) register();
            else if (choice == 3) break;
        }
    }

    private static void login() {
        System.out.print("Phone: ");
        String phone = sc.nextLine();
        System.out.print("PIN: ");
        String pin = sc.nextLine();
        try {
            currentCustomer = authService.login(phone, pin);
            System.out.println("Welcome " + currentCustomer.getFullName());
            customerMenu();
        } catch (Exception e) { System.out.println("Login failed: " + e.getMessage()); }
    }

    private static void register() {
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Phone: ");
        String phone = sc.nextLine();
        System.out.print("PIN (5 digits): ");
        String pin = sc.nextLine();
        try {
            authService.registerCustomer(name, email, phone, pin);
            System.out.println("Registration successful. Please login.");
        } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
    }

    private static void customerMenu() throws Exception {
        while (true) {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("1. Account Management");
            System.out.println("2. Transaction Management");
            System.out.println("3. View My Accounts");
            System.out.println("4. Update Profile");
            System.out.println("5. Change PIN");
            System.out.println("6. Request Loan");
            System.out.println("7. Logout");
            int choice = readInt();
            switch (choice) {
                case 1: accountManagement();
                      break;
                case 2: transactionManagement();
                      break;
                case 3: viewAccounts();
                      break;
                case 4: updateProfile();
                      break;
                case 5: changePin();
                      break;
                case 6: requestLoan();
                     break;
                case 7: currentCustomer = null; return;
            }
        }

    }

    private static void accountManagement() {
        while (true) {
            System.out.println("\n--- Account Management ---");
            System.out.println("1. Create Wallet Account");
            System.out.println("2. Create Savings Account");
            System.out.println("3. View Balance (by account ID)");
            System.out.println("4. Delete Account (balance zero)");
            System.out.println("5. Back");
            int choice = readInt();
            try {
                switch (choice) {
                    case 1:
                        System.out.print("Initial deposit: ");
                        BigDecimal init = new BigDecimal(sc.nextLine());
                        accountService.createWalletAccount(currentCustomer.getId(), init);
                        System.out.println("Wallet created.");
                        break;
                    case 2:
                        System.out.print("Initial deposit: ");
                        BigDecimal initSav = new BigDecimal(sc.nextLine());
                        System.out.print("Monthly withdrawal limit: ");
                        int limit = readInt();
                        System.out.print("Withdrawal fee: ");
                        BigDecimal fee = new BigDecimal(sc.nextLine());
                        accountService.createSavingsAccount(currentCustomer.getId(), initSav, limit, fee);
                        System.out.println("Savings account created.");
                        break;
                    case 3:
                        System.out.print("Account ID: ");
                        int aid = readInt();
                        BigDecimal bal = accountService.getBalance(aid);
                        System.out.println("Balance: " + bal);
                        break;
                    case 4:
                        System.out.print("Account ID to delete: ");
                        int did = readInt();
                        accountService.deleteAccount(did);
                        System.out.println("Account deleted.");
                        break;
                    case 5: return;
                }
            } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
        }
    }

    private static void transactionManagement() {
        while (true) {
            System.out.println("\n--- Transaction Management ---");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Transfer Money");
            System.out.println("4. View Transaction History");
            System.out.println("5. Export to CSV");
            System.out.println("6. Back");
            int choice = readInt();
            try {
                switch (choice) {
                    case 1:
                        System.out.print("Account ID: ");
                        int depAcc = readInt();
                        System.out.print("Amount: ");
                        BigDecimal depAmt = new BigDecimal(sc.nextLine());
                        accountService.deposit(depAcc, depAmt, "Deposit");
                        System.out.println("Deposit successful.");
                        break;
                    case 2:
                        System.out.print("Account ID: ");
                        int witAcc = readInt();
                        System.out.print("Amount: ");
                        BigDecimal witAmt = new BigDecimal(sc.nextLine());
                        accountService.withdraw(witAcc, witAmt, "Withdrawal");
                        System.out.println("Withdrawal successful.");
                        break;
                    case 3:
                        System.out.print("Your wallet account ID: ");
                        int sender = readInt();
                        System.out.print("Recipient phone: ");
                        String recPhone = sc.nextLine();
                        System.out.print("Amount: ");
                        BigDecimal transAmt = new BigDecimal(sc.nextLine());
                        System.out.print("Description: ");
                        String desc = sc.nextLine();
                        String ref = transferService.sendMoney(sender, recPhone, transAmt, desc);
                        System.out.println("Transfer completed. Ref: " + ref);
                        break;
                    case 4:
                        List<Transaction> txns = transactionService.getTransactionHistory(currentCustomer.getId());
                        System.out.println("\nTransaction History:");
                        for (Transaction t : txns) {
                            System.out.printf("%s | %s | %,.2f | %s%n",
                                    t.getReferenceId(), t.getType(), t.getAmount(), t.getCreatedAt());
                        }
                        break;
                    case 5:
                        System.out.print("CSV file path: ");
                        String path = sc.nextLine();
                        csvExportService.exportTransactions(transactionService.getTransactionHistory(currentCustomer.getId()), path);
                        System.out.println("Exported to " + path);
                        break;
                    case 6: return;
                }
            } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
        }
    }

    private static void viewAccounts() throws Exception {
        List<Account> accounts = accountService.getAccountsByCustomer(currentCustomer.getId());
        System.out.println("\nYour Accounts:");
        for (Account a : accounts)
            System.out.printf("ID: %d | %s | Balance: %,.2f%n", a.getId(), a.getAccountType(), a.getBalance());
    }


    private static void updateProfile() {
        try {
            System.out.print("New name (leave blank to keep current): ");
            String name = sc.nextLine();
            System.out.print("New email (leave blank to keep current): ");
            String email = sc.nextLine();
            authService.updateCustomerInfo(currentCustomer.getId(), name, email);
            System.out.println("Profile updated successfully.");
            // Refresh current customer data
            currentCustomer = authService.getCustomerById(currentCustomer.getId());
        } catch (Exception e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    private static void changePin() {
        try {
            System.out.print("Old PIN: ");
            String old = sc.nextLine();
            System.out.print("New PIN (5 digits): ");
            String newPin = sc.nextLine();
            authService.changePin(currentCustomer.getId(), old, newPin);
            System.out.println("PIN changed.");
        } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
    }

    private static void requestLoan() {
        try {
            System.out.print("Amount requested: ");
            BigDecimal amount = new BigDecimal(sc.nextLine());
            boolean approved = loanService.requestLoan(currentCustomer.getId(), amount);
            if (approved) {
                List<Account> accs = accountService.getAccountsByCustomer(currentCustomer.getId());
                int walletId = accs.stream().filter(a -> a.getAccountType().equals("WALLET")).findFirst().get().getId();
                accountService.deposit(walletId, amount, "Loan");
                System.out.println("Loan approved. " + amount + " added to wallet.");
            } else {
                System.out.println("Loan denied. Need more transactions.");
            }
        } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
    }

    private static int readInt() {
        while (true) {
            try { return Integer.parseInt(sc.nextLine()); }
            catch (NumberFormatException e) { System.out.print("Enter a number: "); }
        }
    }
}