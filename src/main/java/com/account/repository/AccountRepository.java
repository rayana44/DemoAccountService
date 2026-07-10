package com.account.repository;

import com.account.entity.Account;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * ENDPOINT 1: Create Account
     * Find account by account number to validate uniqueness
     */
    Optional<Account> findByAccountNumber(String accountNumber);

    /**
     * ENDPOINT 1: Create Account
     * Find account by email to validate uniqueness
     */
    Optional<Account> findByEmail(String email);

    /**
     * ENDPOINT 1: Create Account
     * Find account by mobile number to validate uniqueness
     */
    Optional<Account> findByMobileNumber(String mobileNumber);

    /**
     * ENDPOINT 1: Create Account
     * Check if account number already exists (for validation)
     */
    boolean existsByAccountNumber(String accountNumber);

    /**
     * ENDPOINT 1: Create Account
     * Check if email already exists (for validation)
     */
    boolean existsByEmail(String email);

    /**
     * ENDPOINT 1: Create Account
     * Check if mobile number already exists (for validation)
     */
    boolean existsByMobileNumber(String mobileNumber);

    /**
     * ENDPOINT 3: Update Account
     * Update account details by account number
     */
    @Query("UPDATE Account a SET a.customerName = :customerName, a.email = :email, " +
            "a.mobileNumber = :mobileNumber, a.accountType = :accountType, " +
            "a.cibilScore = :cibilScore, a.branchAddress = :branchAddress " +
            "WHERE a.accountNumber = :accountNumber")
    void updateAccountByAccountNumber(
            @Param("accountNumber") String accountNumber,
            @Param("customerName") String customerName,
            @Param("email") String email,
            @Param("mobileNumber") String mobileNumber,
            @Param("accountType") Object accountType,
            @Param("cibilScore") Integer cibilScore,
            @Param("branchAddress") String branchAddress);

    /**
     * ENDPOINT 6: Credit Amount
     * Update balance by adding credit amount
     */
    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.balance = a.balance + :amount WHERE a.accountNumber = :accountNumber")
    int creditAccountBalance(
            @Param("accountNumber") String accountNumber,
            @Param("amount") BigDecimal amount);

    /**
     * ENDPOINT 7: Debit Amount
     * Update balance by subtracting debit amount
     */
    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.balance = a.balance - :amount WHERE a.accountNumber = :accountNumber")
    void debitAccountBalance(
            @Param("accountNumber") String accountNumber,
            @Param("amount") BigDecimal amount);

    /**
     * Helper Method: Get accounts by customer name
     */
    List<Account> findByCustomerName(String customerName);

    /**
     * Helper Method: Get accounts by account type
     */
    List<Account> findByAccountType(Object accountType);

    /**
     * Helper Method: Get accounts with balance greater than specified amount
     */
    List<Account> findByBalanceGreaterThan(BigDecimal balance);

    /**
     * Helper Method: Get accounts with balance less than specified amount
     */
    List<Account> findByBalanceLessThan(BigDecimal balance);

    /**
     * Helper Method: Get accounts by CIBIL score greater than or equal to
     */
    List<Account> findByCibilScoreGreaterThanEqual(Integer cibilScore);

    /**
     * Helper Method: Get accounts by branch address
     */
    List<Account> findByBranchAddress(String branchAddress);

    /**
     * Helper Method: Delete account by account number
     */
    void deleteByAccountNumber(String accountNumber);

    /**
     * Helper Method: Count total accounts
     */
    long count();

    /**
     * Helper Method: Check if account exists by account number
     */
    boolean existsById(Long id);

    /**
     * Helper Method: Custom query to get account details with all information
     */
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    Optional<Account> findAccountDetailsByAccountNumber(@Param("accountNumber") String accountNumber);

    /**
     * Helper Method: Get accounts created in specific date range (for reporting)
     */
    @Query("SELECT a FROM Account a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    List<Account> findAccountsByCreatedDateRange(
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * Helper Method: Get top accounts by balance
     */
    @Query(value = "SELECT * FROM accounts ORDER BY balance DESC LIMIT :limit", nativeQuery = true)
    List<Account> findTopAccountsByBalance(@Param("limit") int limit);
}
