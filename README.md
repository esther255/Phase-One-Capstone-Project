# IgirePay

IgirePay is a desktop wallet app I built as my Phase One Capstone project. It works like a simple mobile money system where you can send money, save, and track your transactions.

---

## What you can do

- Create an account and log in with a PIN
- Activate a wallet to send and receive money
- Open a savings account
- Send money to someone using their phone number
- Deposit and withdraw from your wallet
- Request a small loan
- See all your transactions and export them to CSV
- Change your PIN anytime

---

## Built with

- Java 21
- JavaFX 21
- PostgreSQL 17
- JDBC
- Maven

---

## How to run it

First, create the database and run the schema:

```sql
CREATE DATABASE igirepay;
```

```bash
psql -U postgres -d igirepay -f src/main/resources/schema.sql
```

Then open `DatabaseConnection.java` and put in your PostgreSQL password:

```java
private static final String PASSWORD = "your_password";
```

Then start the app:

```bash
mvn clean javafx:run
```

---

## Author

Esther — Igire Rwanda Organisation, 2026
