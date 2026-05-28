package com.igirepay.LAB1_model;

import java.time.LocalDateTime;

public class Customer {
    private int id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String pinHash;
    private LocalDateTime createdAt;
    private int failedAttempts;

    public Customer() {}

    public Customer(String fullName, String email, String phoneNumber, String pinHash) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.pinHash = pinHash;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPinHash() { return pinHash; }
    public void setPinHash(String pinHash) { this.pinHash = pinHash; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getFailedAttempts() { return failedAttempts; }
    public void setFailedAttempts(int failedAttempts) { this.failedAttempts = failedAttempts; }

    @Override
    public String toString() {
        return String.format("Customer{id=%d, name='%s', phone='%s', email='%s'}",
                id, fullName, phoneNumber, email);
    }

    public void setPin(String pin) {
        this.pinHash = pin;
    }
}