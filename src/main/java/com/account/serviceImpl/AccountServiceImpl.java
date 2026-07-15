package com.example.accountservice.service;

import com.example.accountservice.dto.CreditCardApplicationRequest;
import com.example.accountservice.entity.Account;
import com.example.accountservice.entity.AccountTransaction;
import com.example.accountservice.repository.AccountRepository;
import com.example.accountservice.repository.AccountTransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountTransactionRepository accountTransactionRepository;

    public AccountServiceImpl(AccountRepository accountRepository,
                              AccountTransactionRepository accountTransactionRepository) {
        this.accountRepository = accountRepository;
        this.accountTransactionRepository = accountTransactionRepository;
    }

    @Override
    public Account createAccount(Account account) {
        Account createdAccount = accountRepository.save(account);
        createOpeningBalanceTransaction(createdAccount);
        return createdAccount;
    }

    @Override
    public Optional<Account> getAccountByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    @Override
    public Optional<List<AccountTransaction>> getTransactionHistory(String accountNumber) {
        if (accountRepository.findByAccountNumber(accountNumber).isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(accountTransactionRepository.findByAccountNumberOrderByTransactionDateDesc(accountNumber));
    }

    @Override
    public void applyForCreditCard(CreditCardApplicationRequest request) {
        // Hook for publishing this request to a card service when messaging is wired in.
    }

    private void createOpeningBalanceTransaction(Account account) {
        if (account.getBalance() == null || account.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        AccountTransaction transaction = new AccountTransaction(
                account.getAccountNumber(),
                account.getCustomerName(),
                "CREDIT",
                account.getBalance(),
                account.getBalance(),
                "Opening balance"
        );
        accountTransactionRepository.save(transaction);
    }
}
