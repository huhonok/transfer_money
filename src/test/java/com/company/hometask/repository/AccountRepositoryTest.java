package com.company.hometask.repository;

import com.company.hometask.service.account.entity.AccountEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountRepositoryTest {

    private AccountRepository accountRepository = new AccountRepositoryImpl();

    @Test
    void saveAccountSuccessful() {
        final AccountEntity accountEntity = new AccountEntity();
        accountEntity.setAmount(100L);
        final AccountEntity savedEntity = accountRepository.save(accountEntity);
        assertNotNull(savedEntity);
        assertNotNull(savedEntity.getId());
        assertEquals(savedEntity.getAmount(), accountEntity.getAmount());
    }

    @Test
    void getAccount() {
        final AccountEntity accountEntity = new AccountEntity();
        accountEntity.setAmount(100L);
        final AccountEntity savedEntity = accountRepository.save(accountEntity);
        final AccountEntity account = accountRepository.getAccount(savedEntity.getId());
        assertNotNull(account);
        assertEquals(account, savedEntity);
    }

    @Test
    void getAccounts() {
        final AccountEntity accountEntity100Amount = new AccountEntity();
        accountEntity100Amount.setAmount(100L);
        final AccountEntity savedAccountEntity100Amount = accountRepository.save(accountEntity100Amount);

        final AccountEntity accountEntity200Amount = new AccountEntity();
        accountEntity100Amount.setAmount(200L);
        final AccountEntity savedAccountEntity200Amount = accountRepository.save(accountEntity200Amount);

        final List<AccountEntity> accounts = accountRepository.getAccounts();

        assertNotNull(accounts);
        assertEquals(accounts.size(), 2);
        assertTrue(accounts.contains(savedAccountEntity100Amount));
        assertTrue(accounts.contains(savedAccountEntity200Amount));
    }

    @Test
    void deleteAccountSuccessful() {
        final AccountEntity accountEntity = new AccountEntity();
        accountEntity.setAmount(100L);
        final AccountEntity savedEntity = accountRepository.save(accountEntity);
        accountRepository.delete(savedEntity.getId());
        final AccountEntity account = accountRepository.getAccount(savedEntity.getId());
        assertNull(account);
    }


}
