package com.account.serviceImpl;

import com.account.entity.Account;
import com.account.repository.AccountRepository;
import com.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.List;
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

            // Business Logic 2: Validate email is unique
            if (accountRepository.existsByEmail(account.getEmail())) {
                logger.error("Email already exists: {}", account.getEmail());
                throw new RuntimeException("Email already registered: " + account.getEmail());
            }

            // Business Logic 3: Validate mobile number is unique
            if (accountRepository.existsByMobileNumber(account.getMobileNumber())) {
                logger.error("Mobile number already exists: {}", account.getMobileNumber());
                throw new RuntimeException("Mobile number already registered: " + account.getMobileNumber());
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
     * ENDPOINT 3: Update Account
     */
    @Override
    public Account updateAccount(String accountNumber, Account accountDetails) {
        logger.info("=== UPDATE ACCOUNT - Updating account: {}", accountNumber);

        try {
            Optional<Account> existingAccount = accountRepository.findByAccountNumber(accountNumber);

            if (existingAccount.isEmpty()) {
                logger.error("Account not found for update: {}", accountNumber);
                return null;
            }

            Account account = existingAccount.get();

            // Update customer name
            if (accountDetails.getCustomerName() != null && !accountDetails.getCustomerName().isEmpty()) {
                account.setCustomerName(accountDetails.getCustomerName());
                logger.info("Updated customer name to: {}", accountDetails.getCustomerName());
            }

            // Validate and update email
            if (accountDetails.getEmail() != null && !accountDetails.getEmail().isEmpty()) {
                Optional<Account> emailExists = accountRepository.findByEmail(accountDetails.getEmail());
                if (emailExists.isPresent() && !emailExists.get().getId().equals(account.getId())) {
                    logger.error("Email already in use: {}", accountDetails.getEmail());
                    throw new RuntimeException("Email already in use by another account");
                }
                account.setEmail(accountDetails.getEmail());
                logger.info("Updated email to: {}", accountDetails.getEmail());
            }

            // Validate and update mobile
            if (accountDetails.getMobileNumber() != null && !accountDetails.getMobileNumber().isEmpty()) {
                Optional<Account> mobileExists = accountRepository.findByMobileNumber(accountDetails.getMobileNumber());
                if (mobileExists.isPresent() && !mobileExists.get().getId().equals(account.getId())) {
                    logger.error("Mobile already in use: {}", accountDetails.getMobileNumber());
                    throw new RuntimeException("Mobile number already in use by another account");
                }
                account.setMobileNumber(accountDetails.getMobileNumber());
                logger.info("Updated mobile number to: {}", accountDetails.getMobileNumber());
            }

            // Update account type
            if (accountDetails.getAccountType() != null) {
                account.setAccountType(accountDetails.getAccountType());
                logger.info("Updated account type to: {}", accountDetails.getAccountType());
            }

            // Validate and update CIBIL
            if (accountDetails.getCibilScore() != null) {
                if (accountDetails.getCibilScore() < 300 || accountDetails.getCibilScore() > 900) {
                    logger.error("Invalid CIBIL score: {}", accountDetails.getCibilScore());
                    throw new RuntimeException("CIBIL score must be between 300 and 900");
                }
                account.setCibilScore(accountDetails.getCibilScore());
                logger.info("Updated CIBIL score to: {}", accountDetails.getCibilScore());
            }

            // Update branch address
            if (accountDetails.getBranchAddress() != null && !accountDetails.getBranchAddress().isEmpty()) {
                account.setBranchAddress(accountDetails.getBranchAddress());
                logger.info("Updated branch address to: {}", accountDetails.getBranchAddress());
            }

            Account updatedAccount = accountRepository.save(account);
            logger.info("Account updated successfully: {}", accountNumber);
            return updatedAccount;

        } catch (Exception e) {
            logger.error("Error updating account", e);
            throw new RuntimeException("Failed to update account: " + e.getMessage());
        }
    }

    /**
     * ENDPOINT 4: Delete Account
     */
    @Override
    public boolean deleteAccount(String accountNumber) {
        logger.info("=== DELETE ACCOUNT - Deleting account: {}", accountNumber);

        try {
            Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);

            if (account.isEmpty()) {
                logger.error("Account not found for deletion: {}", accountNumber);
                return false;
            }

            if (account.get().getBalance().compareTo(BigDecimal.ZERO) > 0) {
                logger.warn("Warning: Deleting account with non-zero balance: {}", account.get().getBalance());
            }

            accountRepository.deleteByAccountNumber(accountNumber);
            logger.info("Account deleted successfully: {}", accountNumber);
            return true;

        } catch (Exception e) {
            logger.error("Error deleting account", e);
            throw new RuntimeException("Failed to delete account: " + e.getMessage());
        }
    }


    /**
     * ENDPOINT 6: Credit Amount
     */
    @Override
    public Account creditAmount(String accountNumber, BigDecimal amount) {
        logger.info("=== CREDIT AMOUNT - Crediting {} to account: {}", amount, accountNumber);

        try {
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                logger.error("Credit amount must be greater than zero. Provided: {}", amount);
                throw new IllegalArgumentException("Credit amount must be greater than zero");
            }

            Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
            if (accountOpt.isEmpty()) {
                logger.error("Account not found for credit: {}", accountNumber);
                return null;
            }

            Account account = accountOpt.get();
            BigDecimal previousBalance = account.getBalance();

            // Use repository method to update balance
            accountRepository.creditAccountBalance(accountNumber, amount);

            // Fetch updated account
            Optional<Account> updatedAccountOpt = accountRepository.findByAccountNumber(accountNumber);
            Account updatedAccount = updatedAccountOpt.get();

            logger.info("Amount credited successfully - Account: {}, Previous Balance: {}, Credit Amount: {}, New Balance: {}",
                    accountNumber, previousBalance, amount, updatedAccount.getBalance());

            return updatedAccount;

        } catch (Exception e) {
            logger.error("Error crediting amount to account", e);
            throw new RuntimeException("Failed to credit amount: " + e.getMessage());
        }
    }

    /**
     * ENDPOINT 7: Debit Amount
     */
    @Override
    public Account debitAmount(String accountNumber, BigDecimal amount) {
        logger.info("=== DEBIT AMOUNT - Debiting {} from account: {}", amount, accountNumber);

        try {
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                logger.error("Debit amount must be greater than zero. Provided: {}", amount);
                throw new IllegalArgumentException("Debit amount must be greater than zero");
            }

            Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
            if (accountOpt.isEmpty()) {
                logger.error("Account not found for debit: {}", accountNumber);
                return null;
            }

            Account account = accountOpt.get();
            BigDecimal previousBalance = account.getBalance();

            // Check for sufficient balance
            if (previousBalance.compareTo(amount) < 0) {
                logger.error("Insufficient balance - Account: {}, Current Balance: {}, Debit Amount: {}",
                        accountNumber, previousBalance, amount);
                throw new RuntimeException("Insufficient balance. Current balance: " + previousBalance);
            }

            // Use repository method to update balance
            accountRepository.debitAccountBalance(accountNumber, amount);

            // Fetch updated account
            Optional<Account> updatedAccountOpt = accountRepository.findByAccountNumber(accountNumber);
            Account updatedAccount = updatedAccountOpt.get();

            logger.info("Amount debited successfully - Account: {}, Previous Balance: {}, Debit Amount: {}, New Balance: {}",
                    accountNumber, previousBalance, amount, updatedAccount.getBalance());

            return updatedAccount;

        } catch (Exception e) {
            logger.error("Error debiting amount from account", e);
            throw new RuntimeException("Failed to debit amount: " + e.getMessage());
        }
    }

    /**
     * ENDPOINT 8: Send Account Data To Card Service
     */
    @Override
    public void sendAccountToKafka(Account account) {
        logger.info("=== SEND TO KAFKA - Sending account data for account: {}", account.getAccountNumber());

        try {
            if (account == null) {
                logger.error("Account object cannot be null");
                throw new IllegalArgumentException("Account object cannot be null");
            }

            if (account.getId() == null) {
                logger.error("Account must be saved before sending to Kafka");
                throw new RuntimeException("Account must be saved before sending to Kafka");
            }

            kafkaProducerService.sendAccountToCardService(account);
            logger.info("Account data sent to Kafka successfully for account: {}", account.getAccountNumber());

        } catch (Exception e) {
            logger.error("Error sending account data to Kafka", e);
            throw new RuntimeException("Failed to send account to Kafka: " + e.getMessage());
        }
    }

    /**
     * Helper: Get all accounts
     */
    @Override
    public List<Account> getAllAccounts() {
        logger.info("=== GET ALL ACCOUNTS");
        return accountRepository.findAll();
    }

    /**
     * Helper: Get account by email
     */
    @Override
    public Optional<Account> getAccountByEmail(String email) {
        logger.info("=== GET ACCOUNT BY EMAIL: {}", email);
        return accountRepository.findByEmail(email);
    }

    /**
     * Helper: Get account by mobile
     */
    @Override
    public Optional<Account> getAccountByMobileNumber(String mobileNumber) {
        logger.info("=== GET ACCOUNT BY MOBILE: {}", mobileNumber);
        return accountRepository.findByMobileNumber(mobileNumber);
    }

//    /**
//     * Helper: Check if account exists
//     */
//    @Override
//    public boolean accountExists(String accountNumber) {
//        logger.info("=== CHECK ACCOUNT EXISTS: {}", accountNumber);
//        return accountRepository.existsByAccountNumber(accountNumber);
//    }
}
