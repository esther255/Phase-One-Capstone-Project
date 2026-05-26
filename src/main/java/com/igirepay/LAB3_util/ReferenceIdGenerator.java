package com.igirepay.LAB3_util;

import java.util.UUID;
import java.util.Scanner;

public class ReferenceIdGenerator {
    public static String generate() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}