package com.leadspace.issuer.dto;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public enum Period {
    DAILY(24L * 60L * 60L, ChronoUnit.DAYS),
    MINUTELY(60L, ChronoUnit.MINUTES),
    HOURLY(60L * 60L, ChronoUnit.HOURS);

    private final long periodInSeconds;
    private final ChronoUnit chronoUnit;

    Period(long periodInSeconds, ChronoUnit chronoUnit) {
        this.periodInSeconds = periodInSeconds;
        this.chronoUnit = chronoUnit;
    }

    public long getDurationInSeconds() {
        return this.periodInSeconds;
    }

    public long nextPeriod() {
        return Instant.now().truncatedTo(chronoUnit).plus(1L, chronoUnit).getEpochSecond();
    }

    public long currPeriod() {
        return Instant.now().truncatedTo(chronoUnit).getEpochSecond();
    }
}
