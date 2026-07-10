package com.account.service;

import com.account.entity.Account;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;

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

    // ============ ENDPOINT 3: Update Account ============
    /**
     * PUT /api/accounts/{accountNumber}
     * Update existing account
     */
    Account updateAccount(String accountNumber, Account accountDetails);

    // ============ ENDPOINT 4: Delete Account ============
    /**
     * DELETE /api/accounts/{accountNumber}
     * Delete account by account number
     */
    boolean deleteAccount(String accountNumber);

    // ============ ENDPOINT 6: Credit Amount ============
    /**
     * POST /api/accounts/credit
     * Credit amount to account
     */
    Account creditAmount(String accountNumber, BigDecimal amount);

    // ============ ENDPOINT 7: Debit Amount ============
    /**
     * POST /api/accounts/debit
     * Debit amount from account
     */
    Account debitAmount(String accountNumber, BigDecimal amount);

    // ============ ENDPOINT 8: Send to Card Service (Kafka) ============
    /**
     * POST /api/accounts/{accountNumber}/send-to-card-service
     * Send account data to Card Service via Kafka
     */
    void sendAccountToKafka(Account account);

    // ============ ADDITIONAL HELPER METHODS ============
    List<Account> getAllAccounts();
    Optional<Account> getAccountByEmail(String email);
    Optional<Account> getAccountByMobileNumber(String mobileNumber);
}
