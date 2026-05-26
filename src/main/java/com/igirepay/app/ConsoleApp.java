package com.igirepay.app;

import com.igirepay.LAB1_service.*;
import com.igirepay.LAB2_dao.*;
import com.igirepay.LAB3_exception.*;
import com.igirepay.LAB1_model.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    private static Scanner scanner = new Scanner(System.in);
    private static AuthService authService;
    private static AccountService accountService;
    private static TransactionService transactionService;
    private static TransferService transferService;
    private static LoanService loanService;
    private static CSVExportService csvExportService;
    private static Customer currentCustomer = null;

    public static void main(String[] args) {
        try {
            initServices();
            mainMenu();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            System.out.println("2. Register New Customer");
            System.out.println("3. Exit");
            System.out.print("Choice: ");
            int choice = readInt();
            switch (choice) {
                case 1:
                    login();
                    if (currentCustomer != null) customerMenu();
                    break;
                case 2:
                    register();
                    break;
                case 3:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void login() {
        System.out.print("Phone number: ");
        String phone = scanner.nextLine();
        System.out.print("PIN: ");
        String pin = scanner.nextLine();
        try {
            currentCustomer = authService.login(phone, pin);
            System.out.println("Welcome " + currentCustomer.getFullName());
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
            currentCustomer = null;
        }
    }

    private static void register() {
        System.out.print("Full name: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Phone number: ");
        String phone = scanner.nextLine();
        System.out.print("PIN (5 digits): ");
        String pin = scanner.nextLine();
        try {
            authService.registerCustomer(name, email, phone, pin);
            System.out.println("Registration successful. Please login.");
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private static void customerMenu() {
        while (true) {
            System.out.println("\n===== CUSTOMER MENU =====");
            System.out.println("1. Account Management");
            System.out.println("2. Transaction Management");
            System.out.println("3. View My Accounts");
            System.out.println("4. Update Profile");
            System.out.println("5. Change PIN");
            System.out.println("6. Loan Request");
            System.out.println("7. Logout");
            System.out.print("Choice: ");
            int choice = readInt();
            switch (choice) {
                case 1: accountManagement(); break;
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
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private static void accountManagement() {
        while (true) {
            System.out.println("\n--- Account Management ---");
            System.out.println("1. Create Wallet Account");
            System.out.println("2. Create Savings Account");
            System.out.println("3. View Balance (by account ID)");
            System.out.println("4. Delete Account (balance must be zero)");
            System.out.println("5. Back");
            System.out.print("Choice: ");
            int choice = readInt();
            try {
                switch (choice) {
                    case 1:
                        System.out.print("Initial deposit: ");
                        BigDecimal initial = new BigDecimal(scanner.nextLine());
                        accountService.createWalletAccount(currentCustomer.getId(), initial);
                        System.out.println("Wallet account created.");
                        break;
                    case 2:
                        System.out.print("Initial deposit: ");
                        BigDecimal initSavings = new BigDecimal(scanner.nextLine());
                        System.out.print("Withdrawal limit per month: ");
                        int limit = readInt();
                        System.out.print("Withdrawal fee: ");
                        BigDecimal fee = new BigDecimal(scanner.nextLine());
                        accountService.createSavingsAccount(currentCustomer.getId(), initSavings, limit, fee);
                        System.out.println("Savings account created.");
                        break;
                    case 3:
                        System.out.print("Account ID: ");
                        int accId = readInt();
                        BigDecimal bal = accountService.getBalance(accId);
                        System.out.println("Balance: " + bal);
                        break;
                    case 4:
                        System.out.print("Account ID to delete: ");
                        int delId = readInt();
                        accountService.deleteAccount(delId);
                        System.out.println("Account deleted (if balance was zero).");
                        break;
                    case 5:
                        return;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void transactionManagement() {
        while (true) {
            System.out.println("\n--- Transaction Management ---");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Transfer Money");
            System.out.println("4. View Transaction History");
            System.out.println("5. Export Transactions to CSV");
            System.out.println("6. Back");
            System.out.print("Choice: ");
            int choice = readInt();
            try {
                switch (choice) {
                    case 1:
                        System.out.print("Account ID: ");
                        int depAcc = readInt();
                        System.out.print("Amount: ");
                        BigDecimal depAmount = new BigDecimal(scanner.nextLine());
                        accountService.deposit(depAcc, depAmount, "Console deposit");
                        System.out.println("Deposit successful.");
                        break;
                    case 2:
                        System.out.print("Account ID: ");
                        int witAcc = readInt();
                        System.out.print("Amount: ");
                        BigDecimal witAmount = new BigDecimal(scanner.nextLine());
                        accountService.withdraw(witAcc, witAmount, "Console withdrawal");
                        System.out.println("Withdrawal successful.");
                        break;
                    case 3:
                        System.out.print("Your wallet account ID (sender): ");
                        int senderId = readInt();
                        System.out.print("Recipient phone number: ");
                        String recipientPhone = scanner.nextLine();
                        System.out.print("Amount: ");
                        BigDecimal transferAmount = new BigDecimal(scanner.nextLine());
                        System.out.print("Description: ");
                        String desc = scanner.nextLine();
                        String ref = transferService.sendMoney(senderId, recipientPhone, transferAmount, desc);
                        System.out.println("Transfer completed. Reference: " + ref);
                        break;
                    case 4:
                        List<Transaction> txns = transactionService.getTransactionHistory(currentCustomer.getId());
                        System.out.println("\n--- Transaction History ---");
                        for (Transaction t : txns) {
                            System.out.printf("%s | %s | %,.2f | %s%n",
                                    t.getReferenceId(), t.getType(), t.getAmount(), t.getCreatedAt());
                        }
                        break;
                    case 5:
                        List<Transaction> allTx = transactionService.getTransactionHistory(currentCustomer.getId());
                        System.out.print("Enter CSV file path: ");
                        String path = scanner.nextLine();
                        csvExportService.exportTransactions(allTx, path);
                        System.out.println("Exported to " + path);
                        break;
                    case 6:
                        return;
                }
            } catch (Exception e) {
                System.out.println("Transaction error: " + e.getMessage());
            }
        }
    }

    private static void viewAccounts() throws Exception {
        List<Account> accounts = accountService.getAccountsByCustomer(currentCustomer.getId());
        System.out.println("\nYour Accounts:");
        for (Account acc : accounts) {
            System.out.printf("ID: %d | Type: %s | Balance: %,.2f%n",
                    acc.getId(), acc.getAccountType(), acc.getBalance());
        }
    }

    private static void updateProfile() {
        try {
            Customer c = authService.getCustomerById(currentCustomer.getId());
            System.out.print("New full name (current: " + c.getFullName() + "): ");
            String newName = scanner.nextLine();
            if (!newName.trim().isEmpty()) c.setFullName(newName);
            System.out.print("New email (current: " + c.getEmail() + "): ");
            String newEmail = scanner.nextLine();
            if (!newEmail.trim().isEmpty()) c.setEmail(newEmail);
            authService.updateCustomerProfile(c);
            System.out.println("Profile updated.");
            currentCustomer = authService.getCustomerById(currentCustomer.getId()); // refresh
        } catch (Exception e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    private static void changePin() {
        try {
            System.out.print("Old PIN: ");
            String oldPin = scanner.nextLine();
            System.out.print("New PIN (5 digits): ");
            String newPin = scanner.nextLine();
            authService.changePin(currentCustomer.getId(), oldPin, newPin);
            System.out.println("PIN changed successfully.");
        } catch (Exception e) {
            System.out.println("PIN change failed: " + e.getMessage());
        }
    }

    private static void requestLoan() {
        try {
            System.out.print("Requested amount: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine());
            boolean approved = loanService.requestLoan(currentCustomer.getId(), amount);
            if (approved) {
                // Disburse to wallet – find wallet account
                List<Account> accs = accountService.getAccountsByCustomer(currentCustomer.getId());
                int walletId = accs.stream().filter(a -> a.getAccountType().equals("WALLET")).findFirst().get().getId();
                accountService.deposit(walletId, amount, "Loan disbursement");
                System.out.println("Loan approved! " + amount + " credited to your wallet.");
            } else {
                System.out.println("Loan denied. Not enough transaction history.");
            }
        } catch (Exception e) {
            System.out.println("Loan request error: " + e.getMessage());
        }
    }

    private static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a number: ");
            }
        }
    }
}