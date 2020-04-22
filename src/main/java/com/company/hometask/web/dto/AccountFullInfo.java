package com.company.hometask.web.dto;

public class AccountFullInfo {

    private Long amount;
    private String id;

    public AccountFullInfo() {
    }

    public AccountFullInfo(Long amount, String id) {
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
}
