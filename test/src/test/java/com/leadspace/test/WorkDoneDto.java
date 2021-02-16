package com.leadspace.test;

public class WorkDoneDto {
    private String client;
    private boolean result;

    public WorkDoneDto(String client, boolean result) {
        this.client = client;
        this.result = result;
    }

    public WorkDoneDto() {
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
