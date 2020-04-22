package com.company.hometask.service.account.entity;

import java.util.Objects;

public class AccountEntity implements Comparable<AccountEntity> {
    private Long amount;
    private String id;

    public AccountEntity() {
    }

    public AccountEntity(Long amount, String id) {
        this.amount = amount;
        this.id = id;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountEntity that = (AccountEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(AccountEntity o) {
        return this.id.compareTo(o.getId());
    }
}
