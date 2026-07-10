# Account Service Implementation Guide

## Project Structure Required
```
src/main/java/com/bank/accountservice/
├── AccountServiceApplication.java
├── controller/
│   └── AccountController.java
├── entity/
│   └── Account.java
├── repository/
│   └── AccountRepository.java
└── service/
    └── AccountService.java

src/main/resources/
└── application.properties
```

## Step 1: Create Directory Structure

Run the `create_dirs.bat` file to create all required directories:
```bash
create_dirs.bat
```

Or manually create these directories:
- `src\main\java\com\bank\accountservice\entity`
- `src\main\java\com\bank\accountservice\repository`
- `src\main\java\com\bank\accountservice\service`
- `src\main\java\com\bank\accountservice\controller`

## Step 2: Create Java Files

### File 1: Account.java (Entity)
Location: `src\main\java\com\bank\accountservice\entity\Account.java`

```java
package com.bank.accountservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String mobileNumber;

    @Column(nullable = false)
    private String accountType;

    @Column(nullable = false)
    private Integer cibilScore;

    @Column(nullable = false)
    private String branchAddress;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

### File 2: AccountRepository.java (Repository)
Location: `src\main\java\com\bank\accountservice\repository\AccountRepository.java`

```java
package com.bank.accountservice.repository;

import com.bank.accountservice.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    Optional<Account> findByEmail(String email);
    Optional<Account> findByMobileNumber(String mobileNumber);
}
```

### File 3: AccountService.java (Service)
Location: `src\main\java\com\bank\accountservice\service\AccountService.java`

```java
package com.bank.accountservice.service;

import com.bank.accountservice.entity.Account;
import com.bank.accountservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Optional<Account> getAccountByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public Optional<Account> getAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public Optional<Account> getAccountByMobileNumber(String mobileNumber) {
        return accountRepository.findByMobileNumber(mobileNumber);
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account updateAccount(Long id, Account accountDetails) {
        Optional<Account> existingAccount = accountRepository.findById(id);
        if (existingAccount.isPresent()) {
            Account account = existingAccount.get();
            account.setCustomerName(accountDetails.getCustomerName());
            account.setEmail(accountDetails.getEmail());
            account.setMobileNumber(accountDetails.getMobileNumber());
            account.setAccountType(accountDetails.getAccountType());
            account.setCibilScore(accountDetails.getCibilScore());
            account.setBranchAddress(accountDetails.getBranchAddress());
            account.setBalance(accountDetails.getBalance());
            return accountRepository.save(account);
        }
        return null;
    }

    public boolean deleteAccount(Long id) {
        if (accountRepository.existsById(id)) {
            accountRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
```

### File 4: AccountController.java (REST Controller)
Location: `src\main\java\com\bank\accountservice\controller\AccountController.java`

```java
package com.bank.accountservice.controller;

import com.bank.accountservice.entity.Account;
import com.bank.accountservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        Account createdAccount = accountService.createAccount(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        Optional<Account> account = accountService.getAccountById(id);
        return account.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<Account> getAccountByAccountNumber(@PathVariable String accountNumber) {
        Optional<Account> account = accountService.getAccountByAccountNumber(accountNumber);
        return account.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Account> getAccountByEmail(@PathVariable String email) {
        Optional<Account> account = accountService.getAccountByEmail(email);
        return account.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/mobile/{mobileNumber}")
    public ResponseEntity<Account> getAccountByMobileNumber(@PathVariable String mobileNumber) {
        Optional<Account> account = accountService.getAccountByMobileNumber(mobileNumber);
        return account.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody Account account) {
        Account updatedAccount = accountService.updateAccount(id, account);
        if (updatedAccount != null) {
            return ResponseEntity.ok(updatedAccount);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        if (accountService.deleteAccount(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
```

### File 5: AccountServiceApplication.java (Main Application)
Location: `src\main\java\com\bank\accountservice\AccountServiceApplication.java`

```java
package com.bank.accountservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }
}
```

## Step 3: Configure application.properties

Update: `src\main\resources\application.properties`

```properties
spring.application.name=account-service
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/bank_accounts
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# Logging
logging.level.root=INFO
logging.level.com.bank.accountservice=DEBUG
```

## API Endpoints Available

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/accounts` | Create new account |
| GET | `/api/accounts` | Get all accounts |
| GET | `/api/accounts/{id}` | Get account by ID |
| GET | `/api/accounts/number/{accountNumber}` | Get account by account number |
| GET | `/api/accounts/email/{email}` | Get account by email |
| GET | `/api/accounts/mobile/{mobileNumber}` | Get account by mobile number |
| PUT | `/api/accounts/{id}` | Update account |
| DELETE | `/api/accounts/{id}` | Delete account |

## Account Entity Structure

**Attributes:**
- `id` - Long (Primary Key, Auto-generated)
- `accountNumber` - String (Unique, Required)
- `customerName` - String (Required)
- `email` - String (Required)
- `mobileNumber` - String (Required)
- `accountType` - String (Required)
- `cibilScore` - Integer (Required)
- `branchAddress` - String (Required)
- `balance` - BigDecimal (Required)
- `createdAt` - LocalDateTime (Auto-set, Immutable)

## Example Request/Response

### Create Account (POST)
```json
{
  "accountNumber": "ACC123456789",
  "customerName": "John Doe",
  "email": "john@example.com",
  "mobileNumber": "9876543210",
  "accountType": "Savings",
  "cibilScore": 750,
  "branchAddress": "123 Main Street, City",
  "balance": 10000.00
}
```

### Response
```json
{
  "id": 1,
  "accountNumber": "ACC123456789",
  "customerName": "John Doe",
  "email": "john@example.com",
  "mobileNumber": "9876543210",
  "accountType": "Savings",
  "cibilScore": 750,
  "branchAddress": "123 Main Street, City",
  "balance": 10000.00,
  "createdAt": "2026-07-09T23:13:22"
}
```

## Database Setup

Create MySQL database:
```sql
CREATE DATABASE bank_accounts;
```

The tables will be created automatically by Hibernate (ddl-auto=update).

## Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

The service will be available at `http://localhost:8080`
