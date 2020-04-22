package com.company.hometask.service.account;

import com.company.hometask.repository.AccountRepository;
import com.company.hometask.service.account.entity.AccountEntity;

import java.util.List;

public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public AccountEntity createAccount(AccountEntity account) {
        return accountRepository.save(account);
    }

    @Override
    public DeleteStatus deleteAccount(String accountId) {
        final AccountEntity deletedAccount = accountRepository.delete(accountId);
        return deletedAccount == null ? DeleteStatus.NOT_FOUND : DeleteStatus.COMPLETED;
    }

    @Override
    public AccountEntity getAccount(String id) {
        return accountRepository.getAccount(id);
    }

    @Override
    public List<AccountEntity> getAccounts() {
        return accountRepository.getAccounts();
    }
}
