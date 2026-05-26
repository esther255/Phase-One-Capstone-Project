package com.igirepay.LAB1_model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Transaction {
    private int id;
    private int accountId;
    private String referenceId;      // unique, used for duplicate prevention
    private String type;             // DEPOSIT, WITHDRAW, SEND_MONEY, RECEIVE_MONEY, SAVINGS_WITHDRAW, etc.
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private Integer senderAccountId;   // for transfers: who sent
    private Integer recipientAccountId;// for transfers: who received
    private String description;

    public Transaction() {}

    public Transaction(int accountId, String referenceId, String type, BigDecimal amount,
                       Integer senderAccountId, Integer recipientAccountId, String description) {
        this.accountId = accountId;
        this.referenceId = referenceId;
        this.type = type;
        this.amount = amount;
        this.senderAccountId = senderAccountId;
        this.recipientAccountId = recipientAccountId;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getSenderAccountId() { return senderAccountId; }
    public void setSenderAccountId(Integer senderAccountId) { this.senderAccountId = senderAccountId; }

    public Integer getRecipientAccountId() { return recipientAccountId; }
    public void setRecipientAccountId(Integer recipientAccountId) { this.recipientAccountId = recipientAccountId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return String.format("Transaction{ref=%s, type=%s, amount=%s, time=%s}",
                referenceId, type, amount, createdAt);
    }
}