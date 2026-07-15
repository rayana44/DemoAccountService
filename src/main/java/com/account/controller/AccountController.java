package com.example.accountservice.controller;

import com.example.accountservice.dto.ApiResponse;
import com.example.accountservice.dto.CreditCardApplicationRequest;
import com.example.accountservice.entity.Account;
import com.example.accountservice.entity.AccountTransaction;
import com.example.accountservice.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/accountdetails/")
@CrossOrigin(origins = "*")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

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

    // 3. Get Transaction History by Account Number
    @GetMapping("/{accountNumber}/transactions")
    public ResponseEntity<ApiResponse<List<AccountTransaction>>> getTransactionHistory(
            @PathVariable String accountNumber) {
        Optional<List<AccountTransaction>> transactions = accountService.getTransactionHistory(accountNumber);
        if (transactions.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>("Transaction history retrieved successfully", 200,
                    transactions.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("Account not found", 404, null));
    }

    // 4. Apply for Credit Card
    @PatchMapping("/apply-credit-card")
    public ResponseEntity<ApiResponse<String>> applyForCreditCard(@RequestBody CreditCardApplicationRequest request) {
        accountService.applyForCreditCard(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ApiResponse<>("Credit card application submitted successfully", 202,
                        "Application sent to card service microservice"));
    }

}
