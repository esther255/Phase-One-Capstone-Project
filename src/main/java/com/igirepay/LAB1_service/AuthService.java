package com.igirepay.LAB1_service;

import com.igirepay.LAB2_dao.AccountDAO;
import com.igirepay.LAB2_dao.CustomerDAO;
import com.igirepay.LAB1_model.Customer;
import com.igirepay.LAB1_model.WalletAccount;
import com.igirepay.LAB3_util.PasswordUtil;
import java.math.BigDecimal;
import java.util.Optional;

public class AuthService {
    private final CustomerDAO customerDAO;
    private final AccountDAO accountDAO;
    private static final int MAX_ATTEMPTS = 3;

    public AuthService(CustomerDAO customerDAO, AccountDAO accountDAO) {
        this.customerDAO = customerDAO;
        this.accountDAO = accountDAO;
    }

    public Customer login(String phone, String pin) throws Exception {
        Optional<Customer> opt = customerDAO.findByPhoneNumber(phone);
        if (opt.isEmpty()) throw new Exception("Customer not found");
        Customer c = opt.get();
        int failed = customerDAO.getFailedAttempts(phone);
        if (failed >= MAX_ATTEMPTS) throw new SecurityException("Account locked");
        if (!PasswordUtil.verifyPin(pin, c.getPinHash())) {
            customerDAO.incrementFailedAttempts(phone);
            throw new SecurityException("Invalid PIN");
        }
        customerDAO.resetFailedAttempts(phone);
        return c;
    }

    public void changePin(int customerId, String oldPin, String newPin) throws Exception {
        Customer c = customerDAO.findById(customerId)
                .orElseThrow(() -> new Exception("Customer not found"));
        if (!PasswordUtil.verifyPin(oldPin, c.getPinHash()))
            throw new SecurityException("Wrong old PIN");
        customerDAO.updatePin(customerId, newPin);
    }

    public Customer getCustomerById(int id) throws Exception {
        return customerDAO.findById(id)
                .orElseThrow(() -> new Exception("Customer not found"));
    }

    public void updateCustomerInfo(int customerId, String newName, String newEmail) throws Exception {
        Customer c = getCustomerById(customerId);
        if (newName != null && !newName.trim().isEmpty()) c.setFullName(newName);
        if (newEmail != null && !newEmail.trim().isEmpty()) c.setEmail(newEmail);
        customerDAO.updateProfile(c);
    }

    public void registerCustomer(String fullName, String email, String phone, String pin) throws Exception {
        if (customerDAO.existsByPhoneNumber(phone))
            throw new Exception("Phone already registered");
        Customer c = new Customer(fullName, email, phone, PasswordUtil.hashPin(pin));
        customerDAO.save(c);

        WalletAccount wallet = new WalletAccount(c.getId(), BigDecimal.ZERO);
        accountDAO.save(wallet);
    }
}