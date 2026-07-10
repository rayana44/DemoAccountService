package com.account.service;

import com.account.dto.CreditCardApplicationRequest;
import com.account.entity.Account;

import java.util.Optional;

public interface AccountService {

    // ============ ENDPOINT 1: Create Account ============

    /**
     * POST /api/accounts
     * Create a new account
     */
    Account createAccount(Account account);

    // ============ ENDPOINT 2: Get Account ============

    /**
     * GET /api/accounts/{accountNumber}
     * Get account by account number
     */
    Optional<Account> getAccountByAccountNumber(String accountNumber);

    // ============ ENDPOINT 3: Apply for Credit Card ============

    /**
     * POST /api/accounts/apply-credit-card
     * Apply for credit card and send account info to cardservice
     */
    void applyForCreditCard(CreditCardApplicationRequest request);


//    List<Account> getAllAccounts();

}
