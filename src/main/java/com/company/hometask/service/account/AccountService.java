package com.company.hometask.service.account;

import com.company.hometask.service.account.entity.AccountEntity;

import java.util.List;

public interface AccountService {
    AccountEntity createAccount(AccountEntity account);

    DeleteStatus deleteAccount(String accountId);

    AccountEntity getAccount(String id);

    List<AccountEntity> getAccounts();
}
