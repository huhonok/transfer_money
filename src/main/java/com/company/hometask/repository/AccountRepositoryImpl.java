package com.company.hometask.repository;

import com.company.hometask.service.account.entity.AccountEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AccountRepositoryImpl implements AccountRepository {
    private final Map<String, AccountEntity> store = new ConcurrentHashMap<>();

    @Override
    public AccountEntity save(AccountEntity account) {
        AccountEntity accountEntity;
        do {
            accountEntity = new AccountEntity(account.getAmount(), UUID.randomUUID().toString());
        }
        while (store.putIfAbsent(accountEntity.getId(), accountEntity) != null);
        return accountEntity;
    }

    @Override
    public AccountEntity delete(String accountId) {
        return store.remove(accountId);
    }

    @Override
    public AccountEntity getAccount(String id) {
        return store.get(id);
    }

    @Override
    public List<AccountEntity> getAccounts() {
        return new ArrayList<>(store.values());
    }
}
