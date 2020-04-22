package com.company.hometask.web.dto;

public class ErrorResponse {
    private int statusCode;
    private String cause;

    public ErrorResponse() {
    }

    public ErrorResponse(int statusCode, String cause) {
        this.statusCode = statusCode;
        this.cause = cause;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }
}
