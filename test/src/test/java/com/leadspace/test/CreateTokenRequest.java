package com.leadspace.test;

public class CreateTokenRequest {
    private String token;
    private Period period;
    private long usage;

    public CreateTokenRequest(String token, Period period, long usage) {
        this.token = token;
        this.period = period;
        this.usage = usage;
    }

    public CreateTokenRequest() {
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public long getUsage() {
        return usage;
    }

    public void setUsage(long usage) {
        this.usage = usage;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
