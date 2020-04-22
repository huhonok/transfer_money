package com.company.hometask.repository;

import com.company.hometask.service.account.entity.AccountEntity;

import java.util.List;

public interface AccountRepository {
    AccountEntity save(AccountEntity account);

    AccountEntity delete(String accountId);

    AccountEntity getAccount(String id);

    List<AccountEntity> getAccounts();

}
