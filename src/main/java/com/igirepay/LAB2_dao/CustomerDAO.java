package com.igirepay.LAB2_dao;

import com.igirepay.LAB1_model.Customer;
import java.util.Optional;

public interface CustomerDAO {
    void save(Customer customer) throws Exception;
    Optional<Customer> findByPhoneNumber(String phoneNumber) throws Exception;
    Optional<Customer> findByEmail(String email) throws Exception;
    Optional<Customer> findById(int id) throws Exception;
    void updateProfile(Customer customer) throws Exception;
    boolean existsByPhoneNumber(String phoneNumber) throws Exception;
    void incrementFailedAttempts(String phoneNumber) throws Exception;
    void resetFailedAttempts(String phoneNumber) throws Exception;
    int getFailedAttempts(String phoneNumber) throws Exception;
    void updatePin(int customerId, String pin) throws Exception;
}