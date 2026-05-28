package com.igirepay.LAB1_service;

import com.igirepay.LAB1_model.Transaction;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

public class CSVExportService {
    public void exportTransactions(List<Transaction> transactions, String filePath) throws Exception {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            pw.println("ID,Reference,Type,Amount,Date,SenderAccount,RecipientAccount,Description");
            for (Transaction t : transactions) {
                pw.printf("%d,%s,%s,%.2f,%s,%s,%s,%s%n",
                        t.getId(), t.getReferenceId(), t.getType(), t.getAmount(),
                        t.getCreatedAt(), t.getSenderAccountId(), t.getRecipientAccountId(), t.getDescription());
            }
        }
    }
}