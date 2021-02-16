package com.leadspace.issuer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZonedDateTime;

public class CreateTokenRequest {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss z")
    private ZonedDateTime from;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss z")
    private ZonedDateTime to;
    private long usage;

    public ZonedDateTime getFrom() {
        return from;
    }

    public void setFrom(ZonedDateTime from) {
        this.from = from;
    }

    public ZonedDateTime getTo() {
        return to;
    }

    public void setTo(ZonedDateTime to) {
        this.to = to;
    }

    public long getUsage() {
        return usage;
    }

    public void setUsage(long usage) {
        this.usage = usage;
    }
}
