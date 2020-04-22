package com.company.hometask.service.account;

import com.company.hometask.repository.AccountRepository;
import com.company.hometask.service.account.entity.AccountEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.company.hometask.service.account.DeleteStatus.COMPLETED;
import static com.company.hometask.service.account.DeleteStatus.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void prepare() {
        Mockito.reset(accountRepository);
    }

    @Test
    void createAccount() {
        final AccountEntity accountEntity = new AccountEntity();
        accountEntity.setAmount(100L);

        final AccountEntity expectedEntity = new AccountEntity();
        expectedEntity.setAmount(100L);
        expectedEntity.setId("11111");

        when(accountRepository.save(accountEntity)).thenReturn(expectedEntity);
        final AccountEntity savedAccount = accountService.createAccount(accountEntity);

        assertNotNull(savedAccount);
        assertEquals(savedAccount, expectedEntity);
    }

    @Test
    void deleteExistingAccount() {
        final AccountEntity expectedEntity = new AccountEntity();
        expectedEntity.setId("id");
        when(accountRepository.delete(expectedEntity.getId())).thenReturn(expectedEntity);
        final DeleteStatus status = accountService.deleteAccount("id");
        assertNotNull(status);
        assertEquals(status, COMPLETED);
    }

    @Test
    void deleteNonExistingAccount() {
        when(accountRepository.delete(eq("id"))).thenReturn(null);
        final DeleteStatus status = accountService.deleteAccount("id");
        assertNotNull(status);
        assertEquals(status, NOT_FOUND);
    }

    @Test
    void getAccountByNonExistingId() {
        when(accountRepository.getAccount("id")).thenReturn(null);
        final AccountEntity accountEntity = accountService.getAccount("id");
        assertNull(accountEntity);
    }

    @Test
    void getAccountByExistingId() {
        final AccountEntity expectedEntity = new AccountEntity();
        expectedEntity.setId("id");
        when(accountRepository.getAccount("id")).thenReturn(expectedEntity);
        final AccountEntity accountEntity = accountService.getAccount("id");
        assertNotNull(accountEntity);
        assertEquals(accountEntity, expectedEntity);
    }

    @Test
    void getEmptyAccountList() {
        when(accountRepository.getAccounts()).thenReturn(new ArrayList<>());
        final List<AccountEntity> accounts = accountService.getAccounts();
        assertNotNull(accounts);
        assertEquals(0, accounts.size());
    }

    @Test
    void getNonEmptyAccountList() {
        when(accountRepository.getAccounts()).thenReturn(List.of(new AccountEntity()));
        final List<AccountEntity> accounts = accountService.getAccounts();
        assertNotNull(accounts);
        assertEquals(1, accounts.size());
    }
}
