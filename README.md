# IgirePay — Desktop Payment Gateway

A secure desktop digital wallet app built with JavaFX and PostgreSQL, inspired by MTN MoMo.

---

## Features

- Register & login with a 5-digit PIN
- Wallet account — send, deposit, withdraw
- Savings account with withdrawal limits and fees
- Transfer money by phone number
- Quick loan (up to 50,000 RWF)
- Transaction history + CSV export
- Duplicate transaction prevention using reference IDs
- Account lockout after 3 failed PIN attempts

---

## Tech Stack

Java 21 · JavaFX 21 · PostgreSQL 17 · JDBC · Maven

---

## Setup

**1. Create the database**
```sql
CREATE DATABASE igirepay;
psql -U postgres -d igirepay -f src/main/resources/schema.sql
```

**2. Update database credentials**

Edit `src/main/java/com/igirepay/LAB2_dao/DatabaseConnection.java`:
```java
private static final String URL      = "jdbc:postgresql://localhost:5432/igirepay";
private static final String USER     = "postgres";
private static final String PASSWORD = "your_password";
```

**3. Run**
```bash
mvn clean javafx:run
```

---

## Author

**Esther**
Igire Rwanda Organisation — Backend Development, Phase One Capstone
2026
