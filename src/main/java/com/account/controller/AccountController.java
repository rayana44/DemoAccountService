package com.account.controller;

import com.account.dto.ApiResponse;
import com.account.dto.CreditDebitRequest;
import com.account.entity.Account;
import com.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // 1. Create Account
    @PostMapping
    public ResponseEntity<ApiResponse<Account>> createAccount(@RequestBody Account account) {
        Account createdAccount = accountService.createAccount(account);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Account created successfully", 201, createdAccount));
    }

    // 2. Get Account by Account Number
    @GetMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<Account>> getAccount(@PathVariable String accountNumber) {
        Optional<Account> account = accountService.getAccountByAccountNumber(accountNumber);
        if (account.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>("Account retrieved successfully", 200, account.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("Account not found", 404, null));
    }

    // 3. Update Account
    @PutMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<Account>> updateAccount(
            @PathVariable String accountNumber,
            @RequestBody Account accountDetails) {
        Account updatedAccount = accountService.updateAccount(accountNumber, accountDetails);
        if (updatedAccount != null) {
            return ResponseEntity.ok(new ApiResponse<>("Account updated successfully", 200, updatedAccount));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("Account not found", 404, null));
    }

    // 4. Delete Account
    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<String>> deleteAccount(@PathVariable String accountNumber) {
        boolean deleted = accountService.deleteAccount(accountNumber);
        if (deleted) {
            return ResponseEntity.ok(new ApiResponse<>("Account deleted successfully", 200, "Account " + accountNumber + " deleted"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("Account not found", 404, null));
    }


    // 6. Credit Amount
    @PostMapping("/credit")
    public ResponseEntity<ApiResponse<Account>> creditAmount(@RequestBody CreditDebitRequest request) {
        Account account = accountService.creditAmount(request.getAccountNumber(), request.getAmount());
        if (account != null) {
            accountService.sendAccountToKafka(account);
            return ResponseEntity.ok(new ApiResponse<>("Amount credited successfully", 200, account));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("Account not found", 404, null));
    }

    // 7. Debit Amount
    @PostMapping("/debit")
    public ResponseEntity<ApiResponse<Account>> debitAmount(@RequestBody CreditDebitRequest request) {
        Account account = accountService.debitAmount(request.getAccountNumber(), request.getAmount());
        if (account != null) {
            accountService.sendAccountToKafka(account);
            return ResponseEntity.ok(new ApiResponse<>("Amount debited successfully", 200, account));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>("Invalid request - Account not found or insufficient balance", 400, null));
    }

    // 8. Send Account Data To Card Service (Kafka)
    @PostMapping("/{accountNumber}/send-to-card-service")
    public ResponseEntity<ApiResponse<String>> sendToCardService(@PathVariable String accountNumber) {
        Optional<Account> account = accountService.getAccountByAccountNumber(accountNumber);
        if (account.isPresent()) {
            accountService.sendAccountToKafka(account.get());
            return ResponseEntity.ok(new ApiResponse<>("Account data sent to Card Service", 200, "Success"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("Account not found", 404, null));
    }
}
