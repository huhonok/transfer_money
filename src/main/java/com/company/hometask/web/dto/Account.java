package com.company.hometask.web.dto;

/**
 * For creation account
 * ID of account generate automatically
 */
public class Account {
    private Long amount;

    public Account() {

    }

    public Account(Long amount) {
        this.amount = amount;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
