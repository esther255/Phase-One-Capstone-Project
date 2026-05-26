package com.igirepay.LAB1_service;

import com.igirepay.LAB2_dao.AccountDAO;
import com.igirepay.LAB2_dao.CustomerDAO;
import com.igirepay.LAB1_model.Account;
import com.igirepay.LAB1_model.Customer;
import com.igirepay.LAB1_model.WalletAccount;
import com.igirepay.LAB3_util.PasswordUtil;
import java.math.BigDecimal;
import java.util.Optional;

public class AuthService {
    private final CustomerDAO customerDAO;
    private final AccountDAO accountDAO;
    private static final int MAX_FAILED_ATTEMPTS = 3;

    public AuthService(CustomerDAO customerDAO, AccountDAO accountDAO) {
        this.customerDAO = customerDAO;
        this.accountDAO = accountDAO;
    }

    // Original login method
    public Customer login(String phoneNumber, String pin) throws Exception {
        Optional<Customer> opt = customerDAO.findByPhoneNumber(phoneNumber);
        if (!opt.isPresent()) {
            throw new IllegalArgumentException("Customer not found");
        }
        Customer customer = opt.get();
        int failed = customerDAO.getFailedAttempts(phoneNumber);
        if (failed >= MAX_FAILED_ATTEMPTS) {
            throw new SecurityException("Account locked. Too many failed attempts.");
        }
        if (!PasswordUtil.verifyPin(pin, customer.getPinHash())) {
            customerDAO.incrementFailedAttempts(phoneNumber);
            throw new SecurityException("Invalid PIN. Attempts left: " + (MAX_FAILED_ATTEMPTS - failed - 1));
        }
        customerDAO.resetFailedAttempts(phoneNumber);
        return customer;
    }

    // Original change PIN method
    public void changePin(int customerId, String oldPin, String newPin) throws Exception {
        Optional<Customer> opt = customerDAO.findById(customerId);
        if (!opt.isPresent()) throw new IllegalArgumentException("Customer not found");
        if (newPin == null || !newPin.matches("\\d{5}")) {
            throw new IllegalArgumentException("New PIN must be exactly 5 digits.");
        }
        Customer customer = opt.get();
        if (!PasswordUtil.verifyPin(oldPin, customer.getPinHash())) {
            throw new SecurityException("Invalid old PIN");
        }
        String newHash = PasswordUtil.hashPin(newPin);
        customerDAO.updatePinHash(customerId, newHash);
    }

    // Get customer by ID
    public Customer getCustomerById(int customerId) throws Exception {
        Optional<Customer> opt = customerDAO.findById(customerId);
        if (!opt.isPresent()) throw new IllegalArgumentException("Customer not found");
        return opt.get();
    }

    public Customer registerCustomer(String fullName, String email, String phoneNumber, String pin) throws Exception {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required.");
        }
        if (pin == null || !pin.matches("\\d{5}")) {
            throw new IllegalArgumentException("PIN must be exactly 5 digits.");
        }
        if (customerDAO.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new IllegalArgumentException("Phone number is already registered.");
        }
        if (customerDAO.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email is already registered.");
        }
        String hash = PasswordUtil.hashPin(pin);
        Customer newCustomer = new Customer(fullName, email, phoneNumber, hash);
        customerDAO.save(newCustomer);

        // Create a wallet account for every newly registered customer.
        Account wallet = new WalletAccount(newCustomer.getId(), BigDecimal.ZERO);
        accountDAO.save(wallet);
        return newCustomer;
    }

    // Update customer profile (name, email)
    public void updateCustomerProfile(Customer customer) throws Exception {
        if (customer.getFullName() == null || customer.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }
        var existingEmailOwner = customerDAO.findByEmail(customer.getEmail());
        if (existingEmailOwner.isPresent() && existingEmailOwner.get().getId() != customer.getId()) {
            throw new IllegalArgumentException("Email is already registered.");
        }
        customerDAO.updateProfile(customer);
    }

    // Get customer name (convenience)
    public String getCustomerName(int customerId) throws Exception {
        Customer c = getCustomerById(customerId);
        return c.getFullName();
    }
}