package com.igirepay.LAB2_dao;
import java.util.Scanner;

public interface ProcessedRequestDAO {
    boolean exists(String referenceId) throws Exception;
    void save(String referenceId) throws Exception;
}