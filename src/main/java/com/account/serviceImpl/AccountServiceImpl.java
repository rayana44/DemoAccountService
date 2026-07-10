package com.account.serviceImpl;

import com.account.dto.CreditCardApplicationRequest;
import com.account.entity.Account;
import com.account.repository.AccountRepository;
import com.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    /**
     * ENDPOINT 1: Create Account
     */
    @Override
    public Account createAccount(Account account) {
        logger.info("=== CREATE ACCOUNT - Starting account creation for: {}", account.getAccountNumber());

        try {
            // Business Logic 1: Validate account number is unique
            if (accountRepository.existsByAccountNumber(account.getAccountNumber())) {
                logger.error("Account number already exists: {}", account.getAccountNumber());
                throw new RuntimeException("Account number already exists: " + account.getAccountNumber());
            }

            // Business Logic 4: Validate CIBIL score
            if (account.getCibilScore() < 300 || account.getCibilScore() > 900) {
                logger.error("Invalid CIBIL score: {}", account.getCibilScore());
                throw new RuntimeException("CIBIL score must be between 300 and 900");
            }

            // Business Logic 5: Validate balance
            if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                logger.error("Balance cannot be negative: {}", account.getBalance());
                throw new RuntimeException("Balance cannot be negative");
            }

            // Business Logic 6: Save account
            Account savedAccount = accountRepository.save(account);
            logger.info("Account created successfully with ID: {}, Account Number: {}",
                    savedAccount.getId(), savedAccount.getAccountNumber());

            return savedAccount;

        } catch (Exception e) {
            logger.error("Error creating account", e);
            throw new RuntimeException("Failed to create account: " + e.getMessage());
        }
    }

    /**
     * ENDPOINT 2: Get Account
     */
    @Override
    public Optional<Account> getAccountByAccountNumber(String accountNumber) {
        logger.info("=== GET ACCOUNT - Fetching account: {}", accountNumber);

        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            logger.error("Account number cannot be null or empty");
            throw new IllegalArgumentException("Account number cannot be null or empty");
        }

        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);

        if (account.isPresent()) {
            logger.info("Account found with number: {}", accountNumber);
        } else {
            logger.warn("Account not found with number: {}", accountNumber);
        }

        return account;
    }

    /**
     * POST /api/accounts/apply-credit-card
     * Apply for credit card and send account info to cardservice
     *
     * @param request
     */
    @Override
    public void applyForCreditCard(CreditCardApplicationRequest request) {
        logger.info("=== APPLY CREDIT CARD - Starting credit card application for account: {}", request.getAccountNumber());

        // Validate request
        if (request.getAccountNumber() == null || request.getAccountNumber().trim().isEmpty()) {
            logger.error("Account number cannot be null or empty");
            throw new IllegalArgumentException("Account number cannot be null or empty");
        }

        // Fetch account details
        Optional<Account> accountOpt = accountRepository.findByAccountNumber(request.getAccountNumber());
        if (!accountOpt.isPresent()) {
            logger.error("Account not found for account number: {}", request.getAccountNumber());
            throw new RuntimeException("Account not found for account number: " + request.getAccountNumber());
        }

        Account account = accountOpt.get();

        // Send to Kafka
        kafkaProducerService.sendCreditCardApplicationToCardService(account, request);
        logger.info("Credit card application sent successfully for account: {}", request.getAccountNumber());
    }

}