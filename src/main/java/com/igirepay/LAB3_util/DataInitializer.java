package com.igirepay.LAB3_util;

import com.igirepay.LAB2_dao.AccountDAO;
import com.igirepay.LAB2_dao.AccountDAOImpl;
import com.igirepay.LAB2_dao.CustomerDAO;
import com.igirepay.LAB2_dao.CustomerDAOImpl;
import com.igirepay.LAB1_model.Customer;
import com.igirepay.LAB1_model.SavingsAccount;
import com.igirepay.LAB1_model.WalletAccount;
import java.math.BigDecimal;

public class DataInitializer {

    public static void initSampleData() {
        try {
            CustomerDAO customerDAO = new CustomerDAOImpl();
            AccountDAO accountDAO = new AccountDAOImpl();


            if (!customerDAO.findByPhoneNumber("0790079144").isPresent()) {
                Customer sample = new Customer("Esther", "esther@gmail.com", "0790079144",
                        PasswordUtil.hashPin("12345"));

                customerDAO.save(sample);
                WalletAccount wallet = new WalletAccount(sample.getId(), BigDecimal.valueOf(100000));
                accountDAO.save(wallet);

                SavingsAccount savings = new SavingsAccount(sample.getId(), BigDecimal.valueOf(50000), 3, BigDecimal.valueOf(500));
                accountDAO.save(savings);

                System.out.println("Sample data inserted successfully.");
            } else {
                System.out.println("Sample customer already exists.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initSampleData(CustomerDAO customerDAO, AccountDAO accountDAO) {
    }
}