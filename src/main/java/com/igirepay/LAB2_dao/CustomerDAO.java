package com.igirepay.LAB2_dao;

import com.igirepay.LAB1_model.Customer;
import java.util.Optional;
import java.util.Scanner;

public interface CustomerDAO {
    void save(Customer customer) throws Exception;
    Optional<Customer> findByPhoneNumber(String phoneNumber) throws Exception;
    Optional<Customer> findByEmail(String email) throws Exception;
    Optional<Customer> findById(int id) throws Exception;
    void updateProfile(Customer customer) throws Exception;
    void updatePin(int customerId, String newPin) throws Exception;
    void incrementFailedAttempts(String phoneNumber) throws Exception;
    void resetFailedAttempts(String phoneNumber) throws Exception;
    int getFailedAttempts(String phoneNumber) throws Exception;
}