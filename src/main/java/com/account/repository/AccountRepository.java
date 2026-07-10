package com.account.repository;

import com.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * ENDPOINT 1: Create Account
     * Find account by account number to validate uniqueness
     */
    Optional<Account> findByAccountNumber(String accountNumber);

    /**
     * ENDPOINT 1: Create Account
     * Check if account number already exists (for validation)
     */
    boolean existsByAccountNumber(String accountNumber);


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

}
