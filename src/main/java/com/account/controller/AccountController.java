package com.account.controller;

import com.account.dto.ApiResponse;
import com.account.dto.CreditCardApplicationRequest;
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

    // 3. Apply for Credit Card
    @PostMapping("/apply-credit-card")
    public ResponseEntity<ApiResponse<String>> applyForCreditCard(@RequestBody CreditCardApplicationRequest request) {
        accountService.applyForCreditCard(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ApiResponse<>("Credit card application submitted successfully", 202,
                        "Application sent to card service microservice"));
    }

}
